package kanban.managers.taskManagers;

import kanban.managers.Managers;
import kanban.managers.taskManagers.exceptions.ManagerException;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskType;
import kanban.utils.Formatter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends kanban.managers.taskManagers.InMemoryTasksManager {
    private final File file;
    private Formatter formatter = new Formatter();

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);

        // reading a file
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            Task task;
            String value = reader.readLine(); // Count 1st line
            int maxId = 0;
            if (value != null) {
                // We read lines in a loop.
                while (!(value = reader.readLine()).isBlank()) { // to empty line
                    task = tasksManager.formatter.tasksFromString(value);
                    tasksManager.addTasksToTheMap(task);

                    if (task.getType() != TaskType.EPIC) {
                        tasksManager.prioritizedTasks.add(task);
                    }

                    if (task.getUin() > maxId) {
                        maxId = task.getUin();
                    }


                }



                String lineHistoryTask = reader.readLine();
                if (lineHistoryTask != null) {
                    List<Integer> idHistoryTask = Formatter.historyFromString(lineHistoryTask);
                    for (Integer id : idHistoryTask) {
                        task = tasksManager.getTaskForHistory(id);
                        tasksManager.historyManager.add(task);
                    }
                }
                tasksManager.id = maxId;
            }
        } catch (IOException e) {
            throw new ManagerException("Произошла ошибка при чтении данных менеджера из файла.");
        }
        return tasksManager;
    }

    protected void save() { // save to file...
        final String header = "id,type,name,status,description,duration,startTime,epic";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(header); // Added a title.
            writer.append(System.lineSeparator());
            saveTasksToFile(writer, tasks);
            saveTasksToFile(writer, epics);
            saveTasksToFile(writer, subtasks);
            writer.write("");
            writer.append(System.lineSeparator());
            final String lineHistoryTask = formatter.historyToString(historyManager);
            writer.write(lineHistoryTask);
        } catch (IOException exception) {
            throw new ManagerException("Произошла ошибка при сохранении данных.");
        }
    }

    //Add tasks to a file.
    private void saveTasksToFile(BufferedWriter writer, Map<Integer, ? extends Task> tasks) throws IOException {
        for (Task task : tasks.values()) {
            String lineTask = formatter.tasksToString(task);
            writer.write(lineTask);
            writer.append(System.lineSeparator());
        }
    }

    private void addTasksToTheMap(Task task) {
        final int idTask = task.getUin(); // task ID's
        switch (task.getType()) {
            case EPIC:
                epics.put(idTask, (Epic) task);
                break;
            case TASK:
                tasks.put(idTask, task);
                break;
            case SUBTASK:
                subtasks.put(idTask, (Subtask) task);
                Epic epic = epics.get(task.getEpicId());
                epic.addListIdSubtasks(idTask);
                break;
        }
    }

    private Task getTaskForHistory(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            return task;
        }

        Epic epic = epics.get(id);
        if (epic != null) {
            return epic;
        }

        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Start application...");

        kanban.managers.taskManagers.TasksManager taskManager = Managers.getDefaultManager();

        System.out.println("Список: " + taskManager.getAllTasks());
        System.out.println();
        System.out.println("Список: " + taskManager.getAllEpics());
        System.out.println();
        System.out.println("Список: " + taskManager.getAllSubtasks());
        System.out.println();
        System.out.println("История: " + taskManager.getHistory());

        Task newTask1 = new Task("Задача №1", "Описание задачи №1", 0, null);
        taskManager.createTask(newTask1);

        Task newTask2 = new Task("Задача №2", "Описание задачи №2"
                , 50, LocalDateTime.of(2022, 12, 27, 20, 0));
        taskManager.createTask(newTask2);

        Epic newEpic1 = new Epic("Эпик №1", "Описание эпика №1");
        taskManager.createEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Подзадача №1 эпика №1","Описание подзадачи №1 эпика №1"
                ,  0, null, newEpic1.getUin());
        taskManager.createSubtask(newSubtask1);

        Subtask newSubtask2 = new Subtask("Подзадача №2 эпика №1", "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0), newEpic1.getUin());
        taskManager.createSubtask(newSubtask2);

        Subtask newSubtask3 = new Subtask("Подзадача №2 эпика №1", "Описание подзадачи №2 эпика №2", 40
                , LocalDateTime.of(2022, 12, 27, 22, 0), newEpic1.getUin());
        taskManager.createSubtask(newSubtask3);

        Epic newEpic2 = new Epic("Эпик №2", "Описание эпика №2");
        taskManager.createEpic(newEpic2);

        System.out.println("Список созданных задач:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        // We request the created tasks several times and check the history for duplicates and order.
        System.out.println("Запрашиваем созданные задачи:");
        System.out.println(taskManager.getEpicById(2));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(6));
        System.out.println(taskManager.getEpicById(3));
        System.out.println("История просмотра: " + taskManager.getHistory());
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }
    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }
    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }
    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }
    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }
    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }
    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }
    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }
    @Override
    public void removeSubtaskByID(Integer id) {
        super.removeSubtaskByID(id);
        save();
    }
}