package kanban;

/*
Sprint 7 "yandexPracticum"
by Dmitry Bartenev
 */

import kanban.managers.Managers;
import kanban.managers.taskManagers.TasksManager;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskStatus;
import kanban.tasks.enums.TaskType;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.Instant;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Start application...");

        var taskManager = Managers.getDefaultManager();

        System.out.println("Список: " + taskManager.getTasks());
        System.out.println();
        System.out.println("Список: " + taskManager.getEpics());
        System.out.println();
        System.out.println("Список: " + taskManager.getSubtasks());
        System.out.println();
        System.out.println("История: " + taskManager.getHistory());

        var task1 = taskManager.createTask(new Task(
                "Task1",
                "Description Task1",
                Instant.ofEpochSecond(10000),
                10));

        var epic1 = taskManager.createEpic(new Epic(
                "Epic1",
                "Description Epic1",
                TaskType.EPIC));

        var subtask1 = taskManager.createSubtask(new Subtask(
                "Subtask1",
                        "Description Subtask1",
                        Instant.EPOCH,
                        50,
                        epic1.getId()));

        var subtask2 = taskManager.createSubtask(new Subtask(
                "Subtask2",
                "Description Subtask2",
                Instant.ofEpochSecond(10000),
                50,
                epic1.getId()));

        System.out.println("Список созданных задач:");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        // We request the created tasks several times and check the history for duplicates and order.
        System.out.println("Запрашиваем созданные задачи:");
        System.out.println(taskManager.getTask(task1.getId()));
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getSubtask(subtask1.getId()));
        System.out.println(taskManager.getSubtask(subtask2.getId()));

        System.out.println("История просмотра: ");
        taskManager.getHistory();

        System.out.println("\n" + "Список задач в порядке приоритета: ");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

    }
}
