package org.example.kanban.manager;

import org.example.kanban.model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idGenerator = 0;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * метод создает новую задачу
     *
     * @param task
     */

    @Override
    public void createTask(Task task) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (task != null) {
            idGenerator++;
            task.setStatus((TaskStatus.NEW)); // при создании статус всегда new
            task.setId(idGenerator);
            tasks.put(idGenerator, task); // добавили задачу в мапу
        }
    }

    /**
     * @param epic
     */
    @Override
    public void createEpic(Epic epic) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (epic != null) {
            idGenerator++;
            epic.setStatus((TaskStatus.NEW)); // при создании статус всегда new
            epic.setId(idGenerator);
            epics.put(idGenerator, epic); // добавили задачу в мапу
        }
    }

    /**
     * @param subtask
     */
    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) { // Проверяем наличие эпика
                idGenerator++;
                subtask.setStatus(TaskStatus.NEW); // При создании статус всегда new
                subtask.setId(idGenerator);
                subtasks.put(idGenerator, subtask); // Добавили подзадачу в мапу
                Epic epic = epics.get(subtasks.get(idGenerator).getEpicId());
                epic.addSubtaskId(idGenerator);
                //добавить id подзадачи в эпик и после уже вызвать метод
                // по обновлению статуса эпика updateStatusEpic
                updateStatusEpic(epic);
            }
        }
    }

    // Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTasks() { // Получение списка всех задач.
        return new ArrayList<>(tasks.values());// Возвращаем список
    }

    @Override
    public ArrayList<Epic> getAllEpics() { // Получение списка всех задач.
        return new ArrayList<>(epics.values());// Возвращаем список
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() { // Получение списка всех задач.
        return new ArrayList<>(subtasks.values()); // Возвращаем список
    }

    //Удаление по идентификатору.
    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
        } else {
            System.out.println("Идентификатор таска указан не верно!");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int idSubtask : epic.getSubtaskIds()) { // удаляем все подзадачи данного эпика
                subtasks.remove(idSubtask);
            }
            epics.remove(id); // Удаляем Эпик
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
        }
    }

    @Override
    public void removeSubtaskByID(Integer id) { // Удаление по идентификатору подзадачи.
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.removeSubtaskId(id);
            subtasks.remove(id); // Удаляем подзадачу
            updateStatusEpic(epic); // Обновляем статус Эпика
        }
    }

    // Удаление всех задач.
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    //Получение по идентификатору.
    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Идентификатор задачи указан не верно!");
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Идентификатор подзадачи указан не верно!");
            return null;
        }
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Subtask> listOfAllSubtasks = new ArrayList<>();
            for (int idSubtask : epic.getSubtaskIds()) {
                listOfAllSubtasks.add(subtasks.get(idSubtask));
            }
            return listOfAllSubtasks;
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
            return null;
        }
    }

    // изменение статуса задач
    // Обновление. Новая версия объекта
    // с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена!");
        }
    }

    @Override
    public void updateEpic(Epic epic) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не найден!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) { // Обновление.
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    //метод на изменения статуса при изменении org.example.kanban.task.Subtask'a

    public void updateStatusEpic(Epic epic) {
        boolean isDone = true;
        boolean isNew = true;
        List<Subtask> subTaskList = getSubtasksFromEpic(epic.getId());
        for (Subtask subTask : subTaskList) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                isDone = false;
                isNew = false;
                break;
            } else if (subTask.getStatus() == TaskStatus.DONE) {
                isNew = false;
                if (!isDone) {
                    break;
                }
            } else if (subTask.getStatus() == TaskStatus.NEW) {
                isDone = false;
                if (!isNew) {
                    break;
                }
            }
        }
        if (epic.getSubtaskIds().size() == 0 || isNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
