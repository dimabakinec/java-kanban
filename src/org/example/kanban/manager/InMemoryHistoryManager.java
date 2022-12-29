package org.example.kanban.manager;
import org.example.kanban.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head; // Указатель на первый элемент списка.
    private Node<Task> tail; // Указатель на последний элемент списка.
    private final Map<Integer, Node<Task>> viewedTasks = new HashMap<>();
    protected static HistoryManager historyManager;

    @Override
    public void add(Task task) {
        Node<Task> node = linkLast(task);
        if (viewedTasks.containsKey(task.getId()))
            removeNode(viewedTasks.get(task.getId()));
        viewedTasks.put(task.getId(), node);
    }

    @Override //удаление задач из списка просмотренных задач
    public void remove(int id) {
        removeNode(viewedTasks.get(id));
        viewedTasks.remove(id);
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> element = head;
        while (element != null) {
            tasksList.add(element.data);
            element = element.next;
        }
        return tasksList;
    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> newNode = new Node<>(tail, task, null);
        if (tail == null)
            head = newNode;
        else
            tail.next = newNode;
        tail = newNode;
        return newNode;
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> nextNode = node.next;
        final Node<Task> prevNode = node.prev;
        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }
        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }
        node.data = null;
    }

    private static class Node<Task> {
        Task data; // Данные внутри элемента.
        Node<Task> next;
        Node<Task> prev; // Ссылка на предыдущий узел.

        Node(Node<Task> prev, Task data, Node<Task> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}
