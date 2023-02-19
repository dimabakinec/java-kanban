package ru.yandex.practicum.tasktracker.service;

import java.io.File;
import java.net.URI;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultNewManager() {
       return FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
    }

    public static TaskManager getDefaultHttpTaskManager() {
        return new HttpTaskManager(URI.create("http://localhost:8078"));
    }
}