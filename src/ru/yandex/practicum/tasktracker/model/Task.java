package ru.yandex.practicum.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.yandex.practicum.tasktracker.model.TypesTasks.TASK;

public class Task {
    protected int uin; // Уникальный идентификационный номер задачи, по которому её можно будет найти.
    private TypesTasks type = TASK; // Тип задачи.
    protected String name; // Название, кратко описывающее суть задачи (например, «Переезд»).
    protected TaskStatus status; // Статус, отображающий её прогресс.
    protected String description; // Описание, в котором раскрываются детали.
    protected Duration duration = Duration.ofMinutes(0); // Продолжительность задачи, оценка того, сколько времени она займёт в минутах (число).
    protected LocalDateTime startTime = null; // Дата и время, когда предполагается приступить к выполнению задачи.
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(int uin, String name, TaskStatus status, String description
            , long duration, LocalDateTime startTime) {
        this.uin = uin;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);;
        this.startTime = startTime;
    }

    public Task(int uin, String name, String description) {
        this.uin = uin;
        this.name = name;
        this.description = description;
    }

    public TypesTasks getType() {
        return type;
    }

    public void setType(TypesTasks type) {
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public Integer getEpicId() {
        return null;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    //метод рассчитывает время завершения задачи, которое рассчитывается исходя из startTime и duration
    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return uin == task.uin && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uin, type);
    }

    @Override
    public String toString() {
       return "Task{" +
               "uin=" + uin +
               ", type=" + type +
               ", name='" + name + '\'' +
               ", status=" + status +
               ", description='" + description +
               ", duration=" + duration.toMinutes() + '\'' +
               ", startTime=" + ((startTime == null) ? "null" : startTime.format(FORMATTER)) +
               ", endTime=" + ((getEndTime() == null) ? "null" : getEndTime().format(FORMATTER)) +
               '}';
    }
}