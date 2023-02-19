package ru.yandex.practicum.tasktracker.service;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    ArrayList<Task> getListedOfAllTasks();

    ArrayList<Epic> getListedOfAllEpics();

    ArrayList<Subtask> getListedOfAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getByIDTask(int id);

    Epic getByIDEpic(int id);

    Subtask getByIDSubtask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskByID(int id);

    void deleteEpicByID(int id);

    void deleteSubtaskByID(Integer id);

    List<Subtask> getListAllSubtasksOfEpic(int id);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}