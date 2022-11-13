package org.example.kanban.manager;
import org.example.kanban.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> taskHistory = new LinkedList<>();
    final int MAX_HISTORY_SIZE = 10;
    
    @Override
    public void add(Task task) {
        taskHistory.add(task);
        if(taskHistory.size() > MAX_HISTORY_SIZE) {
            taskHistory.removeFirst();
        }
    }

    @Override
    public LinkedList<Task> getHistory() { // список просмотренных задач
        return taskHistory;
    }
}
