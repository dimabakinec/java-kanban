package main.java.kanban.tasks;

import main.java.kanban.tasks.enums.TaskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic() {
    }

    public Epic(String name, TaskStatus status, int taskId, String description, ArrayList<Integer> subtaskIds) {
        super(name, status, taskId, description);
        this.subtaskIds = subtaskIds;
    }

    public Epic(int uin, String name, TaskStatus status, String description) {
    }

    public Epic(int id, String name, TaskStatus status, String description, Instant startTime, long duration) {
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
                ", description='" + super.getDescription() +
                '\'' + '}';
    }
}
