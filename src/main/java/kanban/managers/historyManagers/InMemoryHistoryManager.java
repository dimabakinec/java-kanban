package main.java.kanban.managers.historyManagers;

import main.java.kanban.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head; // Указатель на первый элемент списка.
    private Node<Task> tail; // Указатель на последний элемент списка.
    private final Map<Integer, Node<Task>> viewedTasks = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getUin());
            linkLast(task); // Создаем новый узел и добавляем задачу в tail
            viewedTasks.put(task.getUin(), tail);
        }
    }

    @Override //удаление задач из списка просмотренных задач
    public void remove(int id) {
        if (viewedTasks.containsKey(id)) {
            Node<Task> node = viewedTasks.get(id); // Находим узел по id Task.
            removeNode(node); // Вырезаем узел из списка.
            viewedTasks.remove(id); // удаляем запись из Map.
        }
    }

    @Override
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

     static class Node<Task> {
        Task data; // Данные внутри элемента.
        Node<Task> next;
        Node<Task> prev; // Ссылка на предыдущий узел.

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}
