package org.example.kanban;

import org.example.kanban.manager.*;
import org.example.kanban.model.*;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        // создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;

        Task newTask1 = new Task();
        newTask1.setName("Задача №1");
        newTask1.setDescription("Описание задачи №1");
        newTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createTask(newTask1);

        Task newTask2 = new Task();
        newTask2.setName("Задача №2");
        newTask2.setDescription("Описание задачи №2");
        newTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createTask(newTask2);

        Epic newEpic1 = new Epic();
        newEpic1.setName("Эпик №1");
        newEpic1.setDescription("Описание эпика №1");
        newEpic1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createEpic(newEpic1);

        Subtask newSubtask1 = new Subtask();
        newSubtask1.setEpicId(5);
        newSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        newSubtask1.setName("Подзадача №1 эпика №1");
        newSubtask1.setDescription("Описание подзадачи №1 эпика №1");
        taskManager.createSubtask(newSubtask1);

        Subtask newSubtask2 = new Subtask();
        newSubtask2.setEpicId(5);
        newSubtask2.setStatus(TaskStatus.IN_PROGRESS);
        newSubtask2.setName("Подзадача №2 эпика №1");
        newSubtask2.setDescription("Описание подзадачи №2 эпика №1");
        taskManager.createSubtask(newSubtask2);

        Subtask newSubtask3 = new Subtask();
        newSubtask3.setEpicId(5);
        newSubtask3.setStatus(TaskStatus.IN_PROGRESS);
        newSubtask3.setName("Подзадача №3 эпика №1");
        newSubtask3.setDescription("Описание подзадачи №3 эпика №1");
        taskManager.createSubtask(newSubtask3);

        Epic newEpic2 = new Epic();
        newEpic2.setName("Эпик №2");
        newEpic2.setDescription("Описание эпика №2");
        newEpic2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createEpic(newEpic2);

        // запросите созданные задачи несколько раз в разном порядке;

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        // после каждого запроса выведите историю и убедитесь, что в ней нет повторов;

        System.out.println(taskManager.getHistory());
        System.out.println(historyManager.getHistory());

        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(4);

        // удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться;

        // удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.

        //удаляем эпик и задачу по Id
        //taskManager.removeEpicById(3);
        //taskManager.removeTaskById(1);

        //удалить задачу и эпик задачу
        //taskManager.removeAllTasks();
        //taskManager.removeAllEpics();

    }
}
