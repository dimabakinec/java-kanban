package main.java.kanban.tasks;

import main.java.kanban.tasks.enums.TaskStatus;

import java.time.Instant;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, TaskStatus status, int taskId, String description, int epicId) {
        super(name, status, taskId, description);
        this.epicId = epicId;
    }

    public Subtask() {
    }

    public Subtask(int uin, String name, TaskStatus status, String description, long duration, Integer epicId) {
    }

    public Subtask(int id, String name, TaskStatus status, String description, Instant startTime, long duration, int epicID) {
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
