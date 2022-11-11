package org.example.kanban.manager;
import org.example.kanban.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskSumHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if(!taskSumHistory.contains(task)) {
            taskSumHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() { // список просмотренных задач
        return taskSumHistory;
    }
}
