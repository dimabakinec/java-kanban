package org.example.kanban.model;

public class Task {
    private String name;
    private TaskStatus status;
    private Integer id;
    private String description;

    public Task(String name, TaskStatus status, Integer id, String description) {
        this.name = name;
        this.status = status;
        this.id = id;
        this.description = description;
        }

    public Task() {
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        }

    public TaskStatus getStatus() {
        return status;
        }

    public void setStatus(TaskStatus status) {
        this.status = status;
        }

    public int getId() {
        return id;
        }

    public void setId(int id) {
        this.id = id;
        }

    public String getDescription() {
        return description;
        }

    public void setDescription(String description) {
        this.description = description;
        }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
