package org.example.kanban.manager;

import org.example.kanban.model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task); // добавление таска

    void remove (int id); //удаление по id

    List<Task> getHistory(); // получение истории

}
