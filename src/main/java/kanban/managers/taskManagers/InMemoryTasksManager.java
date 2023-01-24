package main.java.kanban.managers.taskManagers;

import main.java.kanban.managers.Managers;
import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.tasks.Epic;
import main.java.kanban.tasks.Subtask;
import main.java.kanban.tasks.Task;
import main.java.kanban.tasks.enums.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTasksManager implements TasksManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private int idGenerator = 0;
    protected int id = 0; // УИН задач

//метод на изменения статуса при изменении Subtask'a
    public void updateStatusEpic(Epic epic) {
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


    @Override
    public List<Task> getHistory() {
        if (historyManager.getHistory() != null) {
            return historyManager.getHistory();
        } else {
            System.out.println("Ошибка! История просмотров не найдена");
            return null;
        }
    }

    @Override
    public void createTask(Task task) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (task != null) {
            idGenerator++;
            task.setStatus(TaskStatus.NEW); // при создании статус всегда new
            task.setUin(idGenerator);
            tasks.put(idGenerator, task); // добавили задачу в мапу
        }
    }

    @Override
    public void createEpic(Epic epic) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (epic != null) {
            idGenerator++;
            epic.setStatus(TaskStatus.NEW); // при создании статус всегда new
            epic.getUin();
            epics.put(idGenerator, epic); // добавили задачу в мапу
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) { // Проверяем наличие эпика
                idGenerator++;
                subtask.setStatus(TaskStatus.NEW); // При создании статус всегда new
                subtask.setUin(idGenerator);
                subtasks.put(idGenerator, subtask); // Добавили подзадачу в мапу
                Epic epic = epics.get(subtasks.get(idGenerator).getEpicId());
                epic.addListIdSubtasks(idGenerator);
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
    public ArrayList<Epic> getAllEpics() { // Получение списка всех эпиков.
        return new ArrayList<>(epics.values());// Возвращаем список
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() { // Получение списка всех субтасков.
        return new ArrayList<>(subtasks.values()); // Возвращаем список
    }

    //Удаление по идентификатору.
    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Идентификатор таска указан не верно!");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int idSubtask : epic.getListIdSubtasks()) { // удаляем все подзадачи данного эпика
                subtasks.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
            epics.remove(id); // Удаляем Эпик
            historyManager.remove(id);
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
        }
    }

    @Override
    public void removeSubtaskByID(Integer id) { // Удаление по идентификатору подзадачи.
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.removeListIdSubtask(id);
            subtasks.remove(id); // Удаляем подзадачу
            historyManager.remove(id);
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
            Task task = tasks.get(id);
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Идентификатор задачи указан не верно!");
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        } else {
            System.out.println("Идентификатор подзадачи указан не верно!");
            return null;
        }
    }

    // изменение статуса задач
    // Обновление. Новая версия объекта
    // с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (tasks.containsKey(task.getUin())) {
            tasks.put(task.getUin(), task);
        } else {
            System.out.println("Задача не найдена!");
        }
    }

    @Override
    public void updateEpic(Epic epic) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (epics.containsKey(epic.getUin())) {
            epics.put(epic.getUin(), epic);
        } else {
            System.out.println("Эпик не найден!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) { // Обновление.
        if (subtasks.containsKey(subtask.getUin()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getUin(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Subtask> subtasksFromEpic = new ArrayList<>();
            for (int idSubtask : epic.getListIdSubtasks()) {
                subtasksFromEpic.add(subtasks.get(idSubtask));
            }
            return subtasksFromEpic;
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
            return null;
        }
    }

}
