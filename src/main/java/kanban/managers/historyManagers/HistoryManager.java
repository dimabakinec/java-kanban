package main.java.kanban.managers.historyManagers;

import main.java.kanban.tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task); // добавление таска

    void remove (int id); //удаление по id

    List<Task> getHistory(); // получение истории

}
