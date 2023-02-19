package ru.yandex.practicum.tasktracker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.yandex.practicum.tasktracker.model.TypesTasks.EPIC;

public class Epic extends Task {
    private LocalDateTime endTime; // дата и время завершения эпика (расчетное).
    private final ArrayList<Integer> listIdSubtasks = new ArrayList<>(); // Список уин подзадач

    public Epic(int uin, String name, TaskStatus status, String description, long duration, LocalDateTime startTime) {
        super(uin, name, status, description, duration, startTime);
        setType(EPIC);
    }

    public Epic(String name, String description) {
        super(name, description);
        setType(EPIC);
    }

    public Epic(int uin, String name, String description) {
        super(uin, name, description);
        setType(EPIC);
    }

    public ArrayList<Integer> getListIdSubtasks() {
        return listIdSubtasks;
    }

    public void removeListIdSubtask(Integer index) {
        listIdSubtasks.remove(index);
    }

    public void addListIdSubtasks(int idSubtask) {
        listIdSubtasks.add(idSubtask);
    }

    public void clearListIdSubtasks() {
        listIdSubtasks.clear();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() { // нужен для информативного результата
        return  "Epic{" +
                "uin=" + uin +
                ", type=" + getType() +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + ((startTime == null) ? "null" : startTime.format(FORMATTER)) +
                ", endTime=" + ((endTime == null) ? "null" : endTime.format(FORMATTER)) +
                ", listIdSubtasks=" + Arrays.asList(listIdSubtasks) +
                '}';
    }
}