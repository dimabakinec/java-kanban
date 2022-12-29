package org.example.kanban.manager;

import org.example.kanban.model.Epic;
import org.example.kanban.model.Subtask;
import org.example.kanban.model.Task;
import java.util.*;

public interface TaskManager {

    List getHistory(); // история просмотра

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskByID(Integer id);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Subtask> getSubtasksFromEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

}
