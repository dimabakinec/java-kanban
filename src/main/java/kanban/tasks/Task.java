package kanban.tasks;

import kanban.tasks.enums.TaskStatus;
import kanban.tasks.enums.TaskType;
import java.time.Instant;
import java.util.Objects;

public class Task {

    protected final String description;
    protected TaskStatus taskStatus;
    protected TaskType taskType;
    protected final String name;
    protected Instant startTime;
    protected long duration;
    protected int id;

    public Task(String name,
                String description,
                Instant startTime,
                long duration) {

        this.taskStatus = taskStatus.NEW;
        this.description = description;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;

    }

    public Task(int id,
                String name,
                TaskStatus taskStatus,
                String description,
                Instant startTime,
                long duration) {

        this.description = description;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.name = name;
        this.id = id;

    }

    public Instant getStartTime() {

        return startTime;

    }

    public Instant getEndTime() {

        final byte SECONDS_IN_ONE_MINUTE = 60;

        return startTime.plusSeconds(duration * SECONDS_IN_ONE_MINUTE);

    }

    public String getName() {

        return name;

    }

    public String getDescription() {

        return description;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public TaskStatus gettaskStatus() {

        return taskStatus;

    }

    public void setTaskStatus(TaskStatus taskStatus) {

        this.taskStatus = taskStatus;

    }

    public TaskType getTaskType() {

        return taskType;

    }

    public void setStartTime(Instant startTime) {

        this.startTime = startTime;

    }

    public long getDuration() {

        return duration;

    }

    public void setDuration(long duration) {

        this.duration = duration;

    }

    @Override
    public String toString() {

        return id + ","
                + taskType + ","
                + name + ","
                + taskStatus + ","
                + description + ","
                + getStartTime() + ","
                + duration + ","
                + getEndTime();

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task that = (Task) o;

        return Objects.equals(this.name, that.name)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.taskStatus, that.taskStatus);

    }


    @Override
    public int hashCode() {

        return Objects.hash(name, description, id, taskStatus);

    }

}