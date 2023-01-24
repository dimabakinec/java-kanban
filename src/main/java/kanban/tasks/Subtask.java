package main.java.kanban.tasks;

import main.java.kanban.tasks.enums.TaskStatus;
import main.java.kanban.tasks.enums.TaskType;
import java.time.LocalDateTime;
import static main.java.kanban.tasks.enums.TaskType.SUBTASK;

public class Subtask extends Task {
    private Integer epicId;
    private final TaskType type = SUBTASK; // Тип задачи.

    public Subtask(int uin, String name, TaskStatus status, String description, long duration, LocalDateTime startTime, Integer epicId) {
        super(uin, name, status, description, duration, startTime);
        this.epicId = epicId;
        setType(SUBTASK);
    }

    public Subtask(String name, String description, long duration, LocalDateTime startTime, Integer epicId) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
        setType(SUBTASK);
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return  "Subtask{" +
                "uin=" + uin +
                ", type=" + getType() +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description +
                ", duration=" + duration.toMinutes() + '\'' +
                ", startTime=" + ((startTime == null) ? "null" : startTime.format(FORMATTER)) +
                ", endTime=" + ((getEndTime() == null) ? "null" : getEndTime().format(FORMATTER)) +
                ", epicId=" + epicId +
                '}';
    }
}