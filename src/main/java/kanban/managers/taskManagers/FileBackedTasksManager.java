package main.java.kanban.managers.taskManagers;

import main.java.kanban.managers.Managers;
import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.managers.taskManagers.exceptions.ManagerSaveException;
import main.java.kanban.tasks.Epic;
import main.java.kanban.tasks.Subtask;
import main.java.kanban.tasks.Task;
import main.java.kanban.tasks.enums.TaskStatus;
import main.java.kanban.tasks.enums.TaskType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.java.kanban.managers.taskManagers.*;
import main.java.kanban.utils.Formatter;


public class FileBackedTasksManager extends InMemoryTasksManager {
    private static final Path filePath = Path.of("src/main/resources/SaveDataFile.csv");

    @Override
    public void updateStatusEpic(Epic epic) {
        super.updateStatusEpic(epic);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
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
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
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
    public ArrayList<Subtask> getSubtasksFromEpic(int id) {
        return super.getSubtasksFromEpic(id);
    }


    // сохранение в файл
    protected void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile()));
             BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            if (br.readLine() == null) {
                String header = "id,type,name,status,description,startTime,duration,endTime,epic" + "\n";
                bw.write(header);
            }
            String values = Formatter.tasksToString(this)
                    + "\n"
                    + Formatter.historyToString(historyManager);
            bw.write(values);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }
    }

    // загрузка из файла
    public static FileBackedTasksManager load(Path filePath) {

        var fileBackedTasksManager = Managers.getDefaultBackedManager();

        int initialID = 0;

        try {

            var fileName = Files.readString(filePath);

            var lines = fileName.split("\n");

            for (int i = 1; i < lines.length - 2; i++) {

                var task = Formatter.tasksFromString(lines[i]);
                var type = lines[i].split(",")[1];

                if (task.getId() > initialID)
                    initialID = task.getId();

                if (TaskType.valueOf(type).equals(TaskType.TASK)) {

                    fileBackedTasksManager.createTask(task);
                    historyManager.add(fileBackedTasksManager.getTaskById(task.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.EPIC)) {

                    var epic = (Epic) task;
                    fileBackedTasksManager.createEpic(epic);
                    historyManager.add(fileBackedTasksManager.getEpicById(epic.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.SUBTASK)) {

                    var subtask = (Subtask) task;
                    fileBackedTasksManager.createSubtask(subtask);
                    historyManager.add(fileBackedTasksManager.getSubtaskById(subtask.getId()));

                }
            }

        } catch (IOException e) {

            throw new ManagerSaveException("Ошибка загрузки из файла");

        }

        return fileBackedTasksManager;

    }


}
