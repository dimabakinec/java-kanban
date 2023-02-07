package kanban.managers.historyManagers;

import kanban.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList historyManager;
    private final Map<Integer, Node<Task>> history;

    public InMemoryHistoryManager() {

        historyManager = new CustomLinkedList();
        history = new HashMap<>();

    }

    @Override
    public void add(Task task) {

        Node<Task> node = historyManager.linkLast(task);

        if (history.containsKey(task.getId()))
            historyManager.removeNode(history.get(task.getId()));

        history.put(task.getId(), node);

    }

    @Override
    public void remove(int id) {

        historyManager.removeNode(history.get(id));
        history.remove(id);

    }

    @Override
    public void clear() {

        history.clear();
        historyManager.clear();

    }

    @Override
    public List<Task> getHistory() {

        return historyManager.getTasks();

    }

}
