package org.example.kanban.manager;
import org.example.kanban.model.Task;
import java.util.*;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
