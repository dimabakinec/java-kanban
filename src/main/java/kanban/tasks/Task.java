package kanban.tasks;

import kanban.tasks.enums.TaskStatus;
import kanban.tasks.enums.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import static kanban.tasks.enums.TaskType.TASK;

public class Task {
    protected int uin; //The unique identification number of the task by which it can be found.
    protected TaskType type = TASK;
    protected String name;
    protected TaskStatus status;
    protected String description;
    protected Duration duration = Duration.ofMinutes(0);
    protected LocalDateTime startTime = null;
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

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
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

    //the method calculates the task completion time, which is calculated based on startTime and duration
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