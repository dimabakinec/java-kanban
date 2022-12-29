package org.example.kanban;

import org.example.kanban.manager.*;
import org.example.kanban.model.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        taskManager.getHistory();

        // создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;

        Task newTask1 = new Task();
        newTask1.setId(1);
        newTask1.setName("Задача №1");
        newTask1.setDescription("Описание задачи №1");
        newTask1.setStatus(TaskStatus.NEW);
        taskManager.createTask(newTask1);

        Task newTask2 = new Task();
        newTask2.setId(2);
        newTask2.setName("Задача №2");
        newTask2.setDescription("Описание задачи №2");
        newTask2.setStatus(TaskStatus.NEW);
        taskManager.createTask(newTask2);

        Epic newEpic1 = new Epic();
        newEpic1.setId(3);
        newEpic1.setName("Эпик №1");
        newEpic1.setDescription("Описание эпика №1");
        newEpic1.setStatus(TaskStatus.NEW);
        taskManager.createEpic(newEpic1);

        Subtask newSubtask1 = new Subtask();
        newSubtask1.setEpicId(3);
        newSubtask1.setStatus(TaskStatus.NEW);
        newSubtask1.setName("Подзадача №1 эпика №1");
        newSubtask1.setDescription("Описание подзадачи №1 эпика №1");
        taskManager.createSubtask(newSubtask1);

        Subtask newSubtask2 = new Subtask();
        newSubtask2.setEpicId(3);
        newSubtask2.setStatus(TaskStatus.NEW);
        newSubtask2.setName("Подзадача №2 эпика №1");
        newSubtask2.setDescription("Описание подзадачи №2 эпика №1");
        taskManager.createSubtask(newSubtask2);

        Subtask newSubtask3 = new Subtask();
        newSubtask3.setEpicId(3);
        newSubtask3.setStatus(TaskStatus.NEW);
        newSubtask3.setName("Подзадача №3 эпика №1");
        newSubtask3.setDescription("Описание подзадачи №3 эпика №1");
        taskManager.createSubtask(newSubtask3);

        Epic newEpic2 = new Epic();
        newEpic2.setId(7);
        newEpic2.setName("Эпик №2");
        newEpic2.setDescription("Описание эпика №2");
        newEpic2.setStatus(TaskStatus.NEW);
        taskManager.createEpic(newEpic2);

        // запросите созданные задачи несколько раз в разном порядке;
        taskManager.getTaskById(1);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");
        taskManager.getTaskById(2);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");
        taskManager.getEpicById(3);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");
        taskManager.getSubtaskById(4);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");
        taskManager.getSubtaskById(5);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");
        taskManager.getSubtaskById(6);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");
        taskManager.getEpicById(7);
        //история просмотров
        System.out.println("История: " + taskManager.getHistory() + "");

//        System.out.println(taskManager.getAllTasks());
//        System.out.println(taskManager.getAllEpics());
//        System.out.println(taskManager.getAllSubtasks());

        // удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться;

        taskManager.removeTaskById(1);
        System.out.println("История после удаления: " + taskManager.getHistory() + "");

        // удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        taskManager.removeEpicById(3);
        System.out.println("История после удаления: " + taskManager.getHistory() + "");

    }
}
