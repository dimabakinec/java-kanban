package kanban.managers;

import kanban.managers.historyManagers.HistoryManager;
import kanban.managers.historyManagers.InMemoryHistoryManager;
import kanban.managers.taskManagers.FileBackedTasksManager;
import kanban.managers.taskManagers.HttpTasksManager;
import kanban.managers.taskManagers.TasksManager;

public class Managers {

    public static TasksManager getDefaultManager() {
        return new HttpTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedManager() {
        return new FileBackedTasksManager();
    }
}
