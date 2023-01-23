package main.java.kanban.managers;

import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.managers.historyManagers.InMemoryHistoryManager;
import main.java.kanban.managers.taskManagers.FileBackedTasksManager;
import main.java.kanban.managers.taskManagers.TasksManager;

public class Managers {

    public static TasksManager getDefaultManager() {
        return new FileBackedTasksManager();
//        return new InMemoryTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

}
