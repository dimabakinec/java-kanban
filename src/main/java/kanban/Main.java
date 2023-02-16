package kanban;

/*
Sprint 8 "yandexPracticum"
by Dmitry Bartenev
 */

import kanban.managers.Managers;
import kanban.managers.taskManagers.HttpTasksManager;
import kanban.servers.KVServer;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskType;

import java.io.IOException;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Start application...");
        new KVServer().start();

        var httpManager = Managers.getDefaultManager();

        var task1 = httpManager.createTask(
                new Task("Task1", "Task1D", Instant.ofEpochSecond(5000), 5));

        var epic1 = httpManager.createEpic(
                new Epic("Epic1", "Epic1D", TaskType.EPIC));

        var subtask1 = httpManager.createSubtask(
                new Subtask("Subtask1", "Subtask1D", Instant.EPOCH, 50, epic1.getId()));

        var subtask2 = httpManager.createSubtask(
                new Subtask("Subtask2", "Subtask2D", Instant.ofEpochSecond(11111), 50, epic1.getId()));

        httpManager.getTask(task1.getId());
        httpManager.getEpic(epic1.getId());
        httpManager.getSubtask(subtask1.getId());
        httpManager.getSubtask(subtask2.getId());
        httpManager.getHistory();

        var refreshedHttpManager = new HttpTasksManager();
        refreshedHttpManager.load();
        refreshedHttpManager.getPrioritizedTasks();

    }
}
