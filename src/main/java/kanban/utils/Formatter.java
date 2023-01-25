package main.java.kanban.utils;

import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.tasks.Epic;
import main.java.kanban.tasks.Subtask;
import main.java.kanban.tasks.Task;
import main.java.kanban.tasks.enums.TaskStatus;
import main.java.kanban.tasks.enums.TaskType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Formatter {

    public static String historyToString(HistoryManager manager) { // Save the history manager to a string.
        List<Task> historyTask = manager.getHistory(); // Pass the list of tasks from the browsing history.
        StringBuilder builder = new StringBuilder();

        if (historyTask.isEmpty()) {
            return "";
        } else {
            builder.append(historyTask.get(0).getUin());
            for (int i = 1; i < historyTask.size(); i++) {
                builder.append(",");
                builder.append(historyTask.get(i).getUin()); // Sequentially add task ids to the list.
            }
            return builder.toString();
        }
    }

    // convert history from string
    public static List<Integer> historyFromString(String value) { // Restoring the id list from CSV for the history manager.
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] split = value.split(",");

            for (String id : split) {
                history.add(Integer.parseInt(id));
            }
        }
        return history;
    }

    // converting all tasks, epics and subtasks into one line, each on a new line
    public String tasksToString(Task task) { //save task to string
        long duration = task.getDuration().toMinutes();
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d", task.getUin(), task.getType(), task.getName()
                , task.getStatus(), task.getDescription(), duration, task.getStartTime(), task.getEpicId());
    }

    // conversion from string back to tasks, epics and subtasks
    public Task tasksFromString(String value) {
        String[] split = value.split(",");
        final int uin = Integer.parseInt(split[0]); // ID
        final TaskType type = TaskType.valueOf(split[1]); // Type of task
        final String name = split[2]; // Название.
        final TaskStatus status = TaskStatus.valueOf(split[3]); // Status
        final String description = split[4];
        final long duration = Long.parseLong(split[5]); // Task duration in minutes.
        LocalDateTime startTime = null;

        if (!split[6].equals("null")) {
            startTime = LocalDateTime.parse(split[6]); // Date and time of the task start.
        }

        switch (type) {
            case SUBTASK:
                final Integer epicId = Integer.valueOf(split[7]);
                return new Subtask(uin, name, status, description, duration, startTime, epicId);
            case TASK:
                return new Task(uin, name, status, description, duration, startTime);
            default:
                return new Epic(uin, name, status, description, duration, startTime);
        }
    }
}