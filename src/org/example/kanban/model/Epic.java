package org.example.kanban.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {
    public Epic() {
    }
    
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, TaskStatus status, int taskId, String description, ArrayList<Integer> subtaskIds) {
        super(name, status, taskId, description);
        this.subtaskIds = subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }
    public void removeSubtaskId(Integer index) {
        subtaskIds.remove(index);
    }

    public void addSubtaskId(int idSubtask) {
        subtaskIds.add(idSubtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + Arrays.asList(subtaskIds) +
                ", nameEpic='" + super.getName() + '\'' +
                ", status=" + super.getStatus() +
                ", taskId=" + super.getId() +
                ", description='" + super.getDescription() + '\'' +
                '}';
    }
}
