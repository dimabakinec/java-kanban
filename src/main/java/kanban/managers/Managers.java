package main.java.kanban.managers;

import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.managers.historyManagers.InMemoryHistoryManager;
import main.java.kanban.managers.taskManagers.FileBackedTasksManager;
import main.java.kanban.managers.taskManagers.InMemoryTasksManager;
import main.java.kanban.managers.taskManagers.TasksManager;
import java.io.File;

public class Managers {

    public static TasksManager getDefaultManager(){
        return new InMemoryTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager(){
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultBackedManager() {
        return new FileBackedTasksManager();
    }
}
