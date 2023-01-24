package main.java.kanban.managers;

import main.java.kanban.managers.historyManagers.HistoryManager;
import main.java.kanban.managers.historyManagers.InMemoryHistoryManager;
import main.java.kanban.managers.taskManagers.FileBackedTasksManager;
import main.java.kanban.managers.taskManagers.TasksManager;
import java.io.File;

public class Managers {
    /* менеджер по умолчанию. чтобы использовать InMemoryTasksManager();
       сменить на другой "return new..." */

    public static TasksManager getDefaultManager() {
        return FileBackedTasksManager.loadFromFile(new File("src/main/resources/SaveDataFile.csv"));
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
