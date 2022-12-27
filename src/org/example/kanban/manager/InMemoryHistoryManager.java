package org.example.kanban.manager;
import org.example.kanban.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head; // Указатель на первый элемент списка.
    private Node<Task> tail; // Указатель на последний элемент списка.
    private final Map<Integer, Node<Task>> viewedTasks = new HashMap<>();

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

    @Override
    public List<Task> getHistory() { // список просмотренных задач
        return getTasks();
    }


    List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node <Task> element = head;
        while (element != null) {
            tasksList.add(element.data);
            element = element.next;
        }
        return tasksList;
    }

    public Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail == null)
            head = newNode;
        else
            tail.next = newNode;
        tail = newNode;
        return newNode;
    }

    public void removeNode(Node<Task> node) {
        if (node == null)
            return;
        if (node.equals(head)) {
            head = node.next;
            if (node.next != null)
                node.next.prev = null;
        } else {
            node.prev.next = node.next;
            if (node.next != null)
                node.next.prev = node.prev;
        }
    }

    static class Node<Task> {
        public Task data; // Данные внутри элемента.
        public Node<Task> next;
        public Node<Task> prev; // Ссылка на предыдущий узел.

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}
