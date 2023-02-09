package kanban.managers.taskManagers;

import kanban.managers.Managers;
import kanban.managers.historyManagers.HistoryManager;
import kanban.managers.taskManagers.exceptions.IntersectionException;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskStatus;

import java.util.*;

public class InMemoryTasksManager implements TasksManager, Comparator<Task> {

    protected static HistoryManager historyManager;
    protected Map<Integer, Subtask> subtasks;
    private Set<Task> prioritizedTasks;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;

    protected int id;

    public InMemoryTasksManager() {

        prioritizedTasks = new TreeSet<>(this);
        historyManager = Managers.getDefaultHistoryManager();
        this.subtasks = new HashMap<>();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();

    }

    public int getNextID() {

        return ++id;

    }

    // получение приоритетного списка + его конвертация из TreeSet в ArrayList
    public List<Task> getPrioritizedTasks() {

        return new ArrayList<>(prioritizedTasks);

    }

    // печать приоритетного списка
    public void printPrioritizedTasks() {

        System.out.println("СПИСОК ПРИОРИТЕТНЫХ ЗАДАЧ: ");
        prioritizedTasks.forEach(System.out::println);

    }

    // добавление таска в список + проверка нет ли пересечения
    private void addToPrioritizedTasks(Task task) {

        prioritizedTasks.add(task);
        checkIntersections();

    }

    // проверка нет ли пересечения
    private void checkIntersections() {

        var prioritizedTasks = getPrioritizedTasks();

        for (int i = 1; i < prioritizedTasks.size(); i++) {

            var prioritizedTask = prioritizedTasks.get(i);

            if (prioritizedTask.getStartTime().isBefore(prioritizedTasks.get(i - 1).getEndTime()))

                throw new IntersectionException("Найдено пересечение между "
                        + prioritizedTasks.get(i)
                        + " и "
                        + prioritizedTasks.get(i - 1));
        }

    }



    @Override // сравнение тасков по getStartTime()
    public int compare(Task o1, Task o2) {

        return o1.getStartTime().compareTo(o2.getStartTime());

    }

    @Override
    public Map<Integer, Task> getTasks() {

        return tasks;

    }

    @Override
    public Map<Integer, Epic> getEpics() {

        return epics;

    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {

        return subtasks;

    }

    @Override
    public Task getTask(int id) {

        var task = tasks.get(id);
        historyManager.add(task);

        return task;

    }

    @Override
    public Epic getEpic(int id) {

        var epic = epics.get(id);
        historyManager.add(epic);

        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {

        var subtask = subtasks.get(id);
        historyManager.add(subtask);

        return subtask;
    }

    @Override
    public Task createTask(Task task) {

        task.setId(getNextID());
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {

        epic.setId(getNextID());
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {

        subtask.setId(getNextID());
        subtasks.put(subtask.getId(), subtask);
        var epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        addToPrioritizedTasks(subtask);
        epic.addSubtask(subtask);
        epic.updateEpicState(subtasks);

        return subtask;

    }

    @Override
    public void removeTask(int id) {

        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(t -> t.getId() == id);

    }

    @Override
    public void removeEpic(int epicID) {

        var epic = epics.get(epicID);

        if (epic == null)
            return;

        for (Integer id : epic.getSubtasks()) {
            prioritizedTasks.removeIf(t -> t.getId() == id);
            subtasks.remove(id);
            historyManager.remove(id);
        }

        epics.remove(epicID);
        historyManager.remove(epicID);

    }

    @Override
    public void removeSubtask(int id) {

        var subtask = subtasks.get(id);

        if (subtask == null)
            return;

        var epic = epics.get(subtask.getEpicID());
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        epic.updateEpicState(subtasks);
        historyManager.remove(id);

    }

    @Override
    public void removeAllTasksEpicsSubtasks() {

        prioritizedTasks.clear();
        historyManager.clear();
        subtasks.clear();
        epics.clear();
        tasks.clear();

    }

    @Override
    public void printAllTasks() {

        if (tasks.isEmpty())
            System.out.println("Список тасков пуст.");

        for (int id : tasks.keySet()) {
            var value = tasks.get(id);
            System.out.println("№" + id + " " + value);
        }

    }

    @Override
    public void printAllSubtasks() {

        if (subtasks.isEmpty())
            System.out.println("Список сабтасков пуст.");

        for (int id : subtasks.keySet()) {
            var value = subtasks.get(id);
            System.out.println("№" + id + " " + value);
        }

    }

    @Override
    public void printAllEpics() {

        if (epics.isEmpty())
            System.out.println("Список эпиков пуст.");

        for (int id : epics.keySet()) {
            var value = epics.get(id);
            System.out.println("№" + id + " " + value);
        }

    }

    @Override
    public Task update(Task task) {

        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);

        return task;

    }

    @Override
    public Subtask update(Subtask subtask) {

        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);
        var epic = epics.get(subtask.getEpicID());
        epic.updateEpicState(subtasks);

        return subtask;

    }

    @Override
    public Epic update(Epic epic) {

        epics.put(epic.getId(), epic);

        return epic;

    }

    @Override
    public List<Task> getHistory() {

        return historyManager.getHistory();

    }

    @Override
    public void printHistory() {

        historyManager.getHistory().forEach(System.out::println);

    }

}
