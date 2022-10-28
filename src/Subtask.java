

public class Subtask extends Task {
    public int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", taskId=" + taskId +
                ", description='" + description + '\'' +
                '}';
    }
}
