package main.java.kanban.utils;

import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.managers.taskManagers.TasksManager;
import main.java.kanban.tasks.Task;
import main.java.kanban.tasks.Epic;
import main.java.kanban.tasks.Subtask;
import main.java.kanban.tasks.enums.TaskType;
import main.java.kanban.tasks.enums.TaskStatus;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Formatter {
    // преобразование истории в строку
    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory())
            sb.append(task.getId()).append(",");
        return sb.toString();
    }


    // преобразование истории из строки
    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (var line : value.split(","))
            history.add(Integer.parseInt(line));
        return history;
    }


    // преобразование всех тасков, эпиков и сабтасков в одну строку, каждая с новой строки
    public static String tasksToString(TasksManager tasksManager) {
        List<Task> allTasks = new ArrayList<>();
        var result = new StringBuilder();
        allTasks.addAll(tasksManager.getAllTasks());
        allTasks.addAll(tasksManager.getAllSubtasks());
        allTasks.addAll(tasksManager.getAllEpics());
        for (var task : allTasks)
            result.append(task.toString()).append("\n");
        return result.toString();
    }

    // преобразование из строки обратно в таски, эпики и сабтаски
    public static Task tasksFromString(String value) {
        var epicID = 0;
        var values = value.split(",");
        var id = Integer.parseInt(values[0]);
        var type = values[1];
        var name = values[2];
        var status = TaskStatus.valueOf(values[3]);
        var description = values[4];
        var startTime = Instant.parse(values[5]);
        var duration = Long.parseLong(values[6]);

        if (TaskType.valueOf(type).equals(TaskType.SUBTASK))
            epicID = Integer.parseInt(values[8]);

        if (TaskType.valueOf(type).equals(TaskType.TASK))
            return new Task(id, name, status, description, startTime, duration);

        if (TaskType.valueOf(type).equals(TaskType.EPIC))
            return new Epic(id, name, status, description, startTime, duration);

        if (TaskType.valueOf(type).equals(TaskType.SUBTASK))
            return new Subtask(id, name, status, description, startTime, duration, epicID);

        else
            throw new IllegalArgumentException("Данный формат таска не поддерживается");

    }



}