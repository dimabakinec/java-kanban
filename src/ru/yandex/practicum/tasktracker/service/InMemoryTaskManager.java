package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    protected Map<Integer, Subtask> subtasks = new HashMap<>(); // Tаблица подзадач
    protected Map<Integer, Task> tasks = new HashMap<>(); // Таблица задач
    protected Map<Integer, Epic> epics = new HashMap<>(); // Таблица эпиков
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime
                    , Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getUin)); // Список задач в порядке приоритета по startTime.

    protected int id = 0; // УИН задач

    private void deleteFromLists(int id, Task task) { //Удаление задач из списка истории и списка задач в порядке приор.
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    private void updateStatusEpic(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;

        if (epic.getListIdSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            for (int idSubtask : epic.getListIdSubtasks()) { // Проверяем статусы задач
                TaskStatus statusSubtask = subtasks.get(idSubtask).getStatus();
                switch (statusSubtask) {
                    case NEW:
                        newStatus++;
                        break;
                    case DONE:
                        doneStatus++;
                        break;
                }
            }
            if (newStatus == epic.getListIdSubtasks().size()) {
                epic.setStatus(TaskStatus.NEW);
            } else if (doneStatus == epic.getListIdSubtasks().size()) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    protected void updateEpicDate(Epic epic) { // метод обновляет дату старта и завершения эпика
        Duration duration = Duration.ofMinutes(0); // Продолжительность.
        LocalDateTime epicStartDate = null; // Дата старта Эпика.
        LocalDateTime epicEndDate = null; // Дата завершения Эпика.

        if (!epic.getListIdSubtasks().isEmpty()) { // Проверяем список подзадач, если пуст считаем - не установленными.
            for (int idSubtask : epic.getListIdSubtasks()) { // Если список подзадач не пуст.
                Subtask subtask = subtasks.get(idSubtask);
                duration = subtask.getDuration().plus(duration);
                LocalDateTime startTime = subtask.getStartTime();
                LocalDateTime endTime = subtask.getEndTime();

                if (startTime != null && (epicStartDate == null || startTime.isBefore(epicStartDate))) {
                    epicStartDate = startTime;
                }
                if (endTime != null && (epicEndDate == null || endTime.isAfter(epicEndDate))) {
                    epicEndDate = endTime;
                }
            }
        }
        epic.setDuration(duration);
        epic.setStartTime(epicStartDate);
        epic.setEndTime(epicEndDate);
    }

    protected void addPrioritizedTasks(Task newTask) { // Добавляем задачу в список prioritizedTasks.
        final LocalDateTime startDateNewTask = newTask.getStartTime();
        final LocalDateTime endDateNewTask = newTask.getEndTime();
        Task taskDouble = null;

        if (prioritizedTasks.isEmpty()) { // если список пуст, то добавляем задачу без проверки.
            prioritizedTasks.add(newTask);
        } else {
            for (Task task : prioritizedTasks) {
                if (task.getUin() == newTask.getUin()) { // если ИД задач одинаковые, не проверяем на пересечение дат.
                    taskDouble = task;
                } else if (startDateNewTask != null){ // проверяем пересечение дат.
                    final LocalDateTime startDate = task.getStartTime();
                    final LocalDateTime endDate = task.getEndTime();
                    if (startDate != null && (startDateNewTask.equals(startDate)
                            || (startDateNewTask.isAfter(startDate) && startDateNewTask.isBefore(endDate))
                            || (startDateNewTask.isBefore(startDate) && endDateNewTask.isAfter(startDate)))) {
                        throw new ManagerException("Обнаружено пересечение планируемой даты старта задачи. " +
                                "Задача не может быть обновлена или добавлена.");
                    }
                }
            }
            if (taskDouble != null) {
                prioritizedTasks.remove(taskDouble);
            }
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void createTask(Task newTask) { // Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (newTask != null) {
            id++;
            newTask.setStatus(TaskStatus.NEW); // При создании статус всегда new
            newTask.setUin(id);
            addPrioritizedTasks(newTask);
            tasks.put(id, newTask); // Добавили задачу в map.
        }
    }

   @Override
    public void createEpic(Epic epic) { // Функция создания эпика. Сам объект должен передаваться в качестве параметра.
        if (epic != null) {
            id++;
            epic.setStatus(TaskStatus.NEW); // При создании статус всегда new
            epic.setUin(id);
            epics.put(id, epic); // Добавили эпик в map.
        }
    }

    @Override
    public void createSubtask(Subtask subtask) { // Функция создания подзадачи.
        /*
        Сам объект должен передаваться в качестве параметра.
        Для каждой подзадачи известно, в рамках какого эпика она выполняется.
        id эпика указывается при создании объекта и передается с объектом.
        Подзадача может создаваться даже если эпик в статусе DONE (уточненное инфо)
        */
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) { // Проверяем наличие эпика
                id++;
                subtask.setStatus(TaskStatus.NEW); // При создании статус всегда new
                subtask.setUin(id);
                addPrioritizedTasks(subtask);
                subtasks.put(id, subtask); // Добавили подзадачу в map.
                Epic epic = epics.get(subtask.getEpicId());
                epic.addListIdSubtasks(id); // Добавили id в список подзадач эпика.
                updateEpicDate(epic);

                if (epic.getStatus() == TaskStatus.DONE) { // Если статус эпика завершен, меняем на "в работе".
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            } else {
                throw new ManagerException("Эпик не создан или не найден!"); // Если эпик не найден.
            }
        }
    }

    @Override
    public ArrayList<Task> getListedOfAllTasks() { // Получение списка всех задач.
        return new ArrayList<>(tasks.values()); // Возвращаем список
    }

    @Override
    public ArrayList<Epic> getListedOfAllEpics() { // Получение списка всех эпиков.
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getListedOfAllSubtasks() { // Получение списка всех подзадач.
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() { // Удаление всех задач.
        if (!tasks.isEmpty()) {
            for (Map.Entry<Integer, Task> entry: tasks.entrySet()) { //Удаляем задачи из истории просмотров и списка.
                deleteFromLists(entry.getKey(), entry.getValue());
            }
            tasks.clear();
        }
    }

    @Override
    public void deleteAllEpics() { // Удаление всех эпиков и соответственно всех подзадач.
        // Согласно ТЗ: "Для каждой подзадачи известно, в рамках какого эпика она выполняется."
        if (!epics.isEmpty()) {
            for (Integer idEpic: epics.keySet()) { // Удаляем эпики из истории просмотров.
                historyManager.remove(idEpic);
            }
            epics.clear();

            if (!subtasks.isEmpty()){
                for (Map.Entry<Integer, Subtask> entry: subtasks.entrySet()) { //Удаляем подзадачи из истории просмотров.
                    deleteFromLists(entry.getKey(), entry.getValue());
                }
                subtasks.clear();
            }
        }
    }

    @Override
    public void deleteAllSubtasks() { // Удаление всех подзадач
        // Согласно ТЗ: "если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW."
        if (!subtasks.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry: subtasks.entrySet()) {
                Epic epic = epics.get(entry.getValue().getEpicId());
                if (!epic.getListIdSubtasks().isEmpty()) { // Если список подзадач Эпика не пуст - очищаем.
                    epic.clearListIdSubtasks();
                    updateStatusEpic(epic);
                    updateEpicDate(epic);
                }
                deleteFromLists(entry.getKey(), entry.getValue()); // Удаляем из списков.
            }
            subtasks.clear(); // Чистим список подзадач.
        }
    }

    @Override
    public Task getByIDTask(int id) { // Получение по идентификатору.
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getByIDEpic(int id) { // Получение по идентификатору.
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getByIDSubtask(int id) { // Получение по идентификатору.
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Task newTask) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (tasks.containsKey(newTask.getUin())) {
            addPrioritizedTasks(newTask);
            tasks.put(newTask.getUin(), newTask);
        } else {
            throw new ManagerException("Задача не найдена!");
        }
    }

    @Override
    public void updateEpic(Epic epic) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (epics.containsKey(epic.getUin())) {
            epics.put(epic.getUin(), epic);
            updateStatusEpic(epic);
            updateEpicDate(epic);
        } else {
            throw new ManagerException("Эпик не найден!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) { // Обновление.
        if (subtasks.containsKey(subtask.getUin())) {
            addPrioritizedTasks(subtask);
            subtasks.put(subtask.getUin(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
            updateEpicDate(epic);
        } else {
            throw new ManagerException("Подзадача не найдена!");
        }
    }

    @Override
    public void deleteTaskByID(int id) { // Удаление по идентификатору задачи.
        if (tasks.containsKey(id)) {
            deleteFromLists(id, tasks.get(id));
            tasks.remove(id);
        } else {
            throw new ManagerException("Идентификатор задачи указан не верно!");
        }
    }

    @Override
    public void deleteEpicByID(int id) {// Удаление по идентификатору эпика.
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int idSubtask : epic.getListIdSubtasks()) { // удаляем все подзадачи данного эпика
                deleteFromLists(idSubtask, subtasks.get(idSubtask));
                subtasks.remove(idSubtask);
            }
            historyManager.remove(id); // Удаляем из истории просмотров.
            epics.remove(id); // Удаляем Эпик.
        } else {
            throw new ManagerException("Идентификатор эпика указан не верно!");
        }
    }

    @Override
    public void deleteSubtaskByID(Integer id) { // Удаление по идентификатору подзадачи.
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.removeListIdSubtask(id); // Удаляем подзадачу из списка в Эпике
            deleteFromLists(id, subtasks.get(id));
            subtasks.remove(id); // Удаляем подзадачу.
            updateStatusEpic(epic); // Обновляем статус Эпика
            updateEpicDate(epic);
        } else {
            throw new ManagerException("Идентификатор подзадачи указан не верно!");
        }
    }

    @Override
    public List<Subtask> getListAllSubtasksOfEpic(int id) { // Получение списка всех подзадач определённого эпика.
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Subtask> subtaskListOfEpic = new ArrayList<>();
            for (int idSubtask : epic.getListIdSubtasks()) {
                subtaskListOfEpic.add(subtasks.get(idSubtask));
            }
            return subtaskListOfEpic;
        }
        return null;
    }

    @Override
    public Set<Task> getPrioritizedTasks() { // Метод возвращает список задач в порядке приоритета по startTime.
        return prioritizedTasks;
    }
}