package kanban.tasks;

import kanban.tasks.enums.TaskStatus;
import kanban.tasks.enums.TaskType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private Instant endTime = Instant.ofEpochSecond(0);
    private final ArrayList<Integer> subtasks;

    public Epic(String name,
                String description,
                TaskType type) {

        super(name, description, Instant.ofEpochSecond(0), 0);
        this.subtasks = new ArrayList<>();
        this.taskType = type;

    }

    public Epic(int id,
                String name,
                TaskStatus taskStatus,
                String description,
                Instant startTime,
                long duration) {

        super(name, description, startTime, duration);
        this.endTime = super.getEndTime();
        this.subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.taskStatus = taskStatus;
        this.id = id;

    }

    // обновление состояния эпика
    public void updateEpicState(Map<Integer, Subtask> subs) {

        var startTime = subs.get(subtasks.get(0)).getStartTime();
        var endTime = subs.get(subtasks.get(0)).getEndTime();

        int isNew = 0;
        int isDone = 0;

        for (var id : getSubtasks()) {

            var subtask = subs.get(id);

            if (subtask.gettaskStatus() == TaskStatus.NEW)
                isNew += 1;

            if (subtask.gettaskStatus() == TaskStatus.DONE)
                isDone += 1;

            if (subtask.getStartTime().isBefore(startTime))
                startTime = subtask.getStartTime();

            if (subtask.getEndTime().isAfter(endTime))
                endTime = subtask.getEndTime();
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = Duration.between(startTime, endTime).toMinutes();

        if (getSubtasks().size() == isNew) {

            setTaskStatus(TaskStatus.NEW);

            return;

        } else if (getSubtasks().size() == isDone) {

            setTaskStatus(TaskStatus.DONE);

            return;

        }

        setTaskStatus(TaskStatus.IN_PROGRESS);

    }

    public ArrayList<Integer> getSubtasks() {

        return subtasks;

    }

    public void addSubtask(Subtask subtask) {

        subtasks.add(subtask.getId());

    }

    public void setEndTime(Instant endTime) {

        this.endTime = endTime;

    }

    @Override
    public Instant getEndTime() {

        return endTime;

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
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;

        Epic that = (Epic) o;

        return Objects.equals(this.subtasks, that.subtasks);

    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), subtasks);

    }
}