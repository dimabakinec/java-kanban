package kanban.managers.taskManagers;

import kanban.managers.Managers;
import kanban.managers.historyManagers.HistoryManager;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskStatus;

import java.util.*;

public class InMemoryTasksManager implements TasksManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime
                    , Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getUin)); // Список задач в порядке приоритета по startTime.



    private int idGenerator = 0;
    protected int id = 0; // uin tasks

    public void updateStatusEpic(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;

        if (epic.getListIdSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            for (int idSubtask : epic.getListIdSubtasks()) { //Checking task statuses
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
    public void createTask(Task task) {
        if (task != null) {
            idGenerator++;
            task.setStatus(TaskStatus.NEW);
            task.setUin(idGenerator);
            tasks.put(idGenerator, task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            idGenerator++;
            epic.setStatus(TaskStatus.NEW);
            epic.getUin();
            epics.put(idGenerator, epic);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) {
                idGenerator++;
                subtask.setStatus(TaskStatus.NEW);
                subtask.setUin(idGenerator);
                subtasks.put(idGenerator, subtask);
                Epic epic = epics.get(subtasks.get(idGenerator).getEpicId());
                epic.addListIdSubtasks(idGenerator);
                updateStatusEpic(epic);
            }
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

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
            for (int idSubtask : epic.getListIdSubtasks()) { // delete all subtasks of this epic
                historyManager.remove(idSubtask);
            }
            epics.remove(id); // Удаляем Эпик
            historyManager.remove(id);
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
        }
    }

    @Override
    public void removeSubtaskByID(Integer id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.removeListIdSubtask(id);
            subtasks.remove(id);
            historyManager.remove(id);
            updateStatusEpic(epic);
        }
    }

    // Deleting all tasks.
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

    //Get by ID.
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

    /* change the status of tasks
    Update. New version of the object
    with the correct identifier is passed as a parameter. */
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getUin())) {
            tasks.put(task.getUin(), task);
        } else {
            System.out.println("Задача не найдена!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getUin())) {
            epics.put(epic.getUin(), epic);
        } else {
            System.out.println("Эпик не найден!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getUin()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getUin(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() { // Метод возвращает список задач в порядке приоритета по startTime.
        return prioritizedTasks;
    }

    //Getting a list of all subtasks of a specific epic.
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
