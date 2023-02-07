package kanban.managers;

import kanban.managers.historyManagers.HistoryManager;
import kanban.managers.historyManagers.InMemoryHistoryManager;
import kanban.managers.taskManagers.FileBackedTasksManager;
import kanban.managers.taskManagers.InMemoryTasksManager;

public class Managers {

    public static InMemoryTasksManager getDefaultMemoryManager(){
        return new InMemoryTasksManager();
    }

    public static FileBackedTasksManager getDefaultManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
