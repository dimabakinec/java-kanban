package kanban.managers.taskManagers;

import kanban.managers.Managers;
import kanban.managers.taskManagers.exceptions.ManagerSaveException;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskType;
import kanban.utils.Formatter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {

    private static final Path filePath = Path.of("src/main/resources/SaveDataFile.csv");

    @Override
    public Task createTask(Task task) {

        var savedTask = super.createTask(task);
        save();

        return savedTask;

    }

    @Override
    public Epic createEpic(Epic epic) {

        var savedEpic = super.createEpic(epic);
        save();

        return savedEpic;

    }

    @Override
    public Subtask createSubtask(Subtask subtask) {

        var savedSubtask = super.createSubtask(subtask);
        save();

        return savedSubtask;

    }

    @Override
    public void removeTask(int id) {

        super.removeTask(id);
        save();

    }

    @Override
    public void removeEpic(int epicID) {

        super.removeEpic(epicID);
        save();

    }

    @Override
    public void removeSubtask(int id) {

        super.removeSubtask(id);
        save();

    }

    @Override
    public void removeAllTasksEpicsSubtasks() {

        super.removeAllTasksEpicsSubtasks();
        save();

    }

    @Override
    public Task update(Task task) {

        var updatedTask = super.update(task);
        save();

        return updatedTask;

    }

    @Override
    public Epic update(Epic epic) {

        var updatedEpic = super.update(epic);
        save();

        return updatedEpic;

    }

    @Override
    public Subtask update(Subtask subtask) {

        var updatedSubtask = super.update(subtask);
        save();

        return updatedSubtask;

    }

    @Override
    public Task getTask(int id) {

        var savedTask = super.getTask(id);
        save();

        return savedTask;

    }

    @Override
    public Epic getEpic(int id) {

        var savedEpic = super.getEpic(id);
        save();

        return savedEpic;

    }

    @Override
    public Subtask getSubtask(int id) {

        var savedSubtask = super.getSubtask(id);
        save();

        return savedSubtask;

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

        var fileBackedTasksManager = Managers.getDefaultManager();

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
                    historyManager.add(fileBackedTasksManager.getTask(task.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.EPIC)) {

                    var epic = (Epic) task;
                    fileBackedTasksManager.createEpic(epic);
                    historyManager.add(fileBackedTasksManager.getEpic(epic.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.SUBTASK)) {

                    var subtask = (Subtask) task;
                    fileBackedTasksManager.createSubtask(subtask);
                    historyManager.add(fileBackedTasksManager.getSubtask(subtask.getId()));

                }
            }

        } catch (IOException e) {

            throw new ManagerSaveException("Ошибка загрузки из файла");

        }

        return fileBackedTasksManager;

    }

}