package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.server.KVServer;
import ru.yandex.practicum.tasktracker.service.HttpTaskManager;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Поехали!");
        new KVServer().start();

        HttpTaskManager taskManager = new HttpTaskManager(URI.create("http://localhost:8078"));
        taskManager.loadFromServer();

        System.out.println("Список: " + taskManager.getListedOfAllEpics());
        System.out.println();
        System.out.println("Список: " + taskManager.getListedOfAllTasks());
        System.out.println();
        System.out.println("Список: " + taskManager.getListedOfAllSubtasks());
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
        System.out.println(taskManager.getListedOfAllTasks());
        System.out.println(taskManager.getListedOfAllEpics());
        System.out.println(taskManager.getListedOfAllSubtasks());

        // Запрашиваем созданные задачи несколько раз и проверяем историю на дубли и порядок.
        System.out.println("Запрашиваем созданные задачи:");
        System.out.println(taskManager.getByIDEpic(7));
        System.out.println(taskManager.getByIDSubtask(4));
        System.out.println(taskManager.getByIDSubtask(5));
        System.out.println(taskManager.getByIDTask(1));
        System.out.println(taskManager.getByIDSubtask(4));
        System.out.println(taskManager.getByIDSubtask(6));
        System.out.println(taskManager.getByIDEpic(3));
        System.out.println("История просмотра: " + taskManager.getHistory());
        System.out.println("\n" + "Список задач в порядке приоритета: ");
        taskManager.getPrioritizedTasks().forEach(System.out::println);
    }
}