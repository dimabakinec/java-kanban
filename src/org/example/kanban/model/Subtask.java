package org.example.kanban.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, TaskStatus status, int taskId, String description, int epicId) {
        super(name, status, taskId, description);
        this.epicId = epicId;
    }

    public Subtask() {
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
