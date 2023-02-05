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
import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedIOException {
        System.out.println("Start application...");

        TasksManager taskManager = Managers.getDefaultManager();

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
        System.out.println(taskManager.getEpicById(1));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(6));
        System.out.println(taskManager.getEpicById(3));
        System.out.println("История просмотра: " + taskManager.getHistory());

        System.out.println("\n" + "Список задач в порядке приоритета: ");
        taskManager.getPrioritizedTasks().forEach(System.out::println);
    }
}
