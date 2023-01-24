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

    // преобразование истории в строку
    public static String historyToString(HistoryManager manager) { // Сохранение менеджера истории в строку.
        List<Task> historyTask = manager.getHistory(); // Передаем список задач из истории просмотров.
        StringBuilder builder = new StringBuilder();

        if (historyTask.isEmpty()) {
            return "";
        } else {
            builder.append(historyTask.get(0).getUin());
            for (int i = 1; i < historyTask.size(); i++) {
                builder.append(",");
                builder.append(historyTask.get(i).getUin()); // Последовательно добавляем в список id задач.
            }
            return builder.toString();
        }
    }

    // преобразование истории из строки
    public static List<Integer> historyFromString(String value) { // Восстановление списка id из CSV для менеджера истории.
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] split = value.split(",");

            for (String id : split) {
                history.add(Integer.parseInt(id));
            }
        }
        return history;
    }

    // преобразование всех тасков, эпиков и сабтасков в одну строку, каждая с новой строки
    public String tasksToString(Task task) { //Сохранение задачи в строку.
        long duration = task.getDuration().toMinutes();
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d", task.getUin(), task.getType(), task.getName()
                , task.getStatus(), task.getDescription(), duration, task.getStartTime(), task.getEpicId());
    }

    // преобразование из строки обратно в таски, эпики и сабтаски
    public Task tasksFromString(String value) { //Создание задачи из строки.
        String[] split = value.split(",");
        final int uin = Integer.parseInt(split[0]); // ID задачи.
        final TaskType type = TaskType.valueOf(split[1]); // Тип задачи.
        final String name = split[2]; // Название.
        final TaskStatus status = TaskStatus.valueOf(split[3]); // Статус.
        final String description = split[4]; // Описание.
        final long duration = Long.parseLong(split[5]); // Продолжительность задачи в минутах.
        LocalDateTime startTime = null;

        if (!split[6].equals("null")) {
            startTime = LocalDateTime.parse(split[6]); // Дата и время старта задачи.
        }

        switch (type) {
            case SUBTASK:
                final Integer epicId = Integer.valueOf(split[7]); // ID эпика.
                return new Subtask(uin, name, status, description, duration, startTime, epicId);
            case TASK:
                return new Task(uin, name, status, description, duration, startTime);
            default:
                return new Epic(uin, name, status, description, duration, startTime);
        }
    }
}