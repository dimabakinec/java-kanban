package kanban.managers.historyManagers;

import kanban.tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task); // adding a task

    void remove(int id); // elete by id

    List<Task> getHistory(); // getting history
}
