package task;

import java.util.Objects;

public class Subtask extends Task {

    private final Integer epicId;     // Id эпика, к которому относится подзадача

    public Subtask(Integer id, String title, String description, Integer epicId) {
        super(id, title, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, String description, Integer epicId, Status status) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription().length() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getId(), epicId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask otherTask = (Subtask) obj;
        return Objects.equals(getTitle(), otherTask.getTitle()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                Objects.equals(getStatus(), otherTask.getStatus()) &&
                Objects.equals(getId(), otherTask.getId()) &&
                Objects.equals(epicId, otherTask.epicId);
    }

    public Integer getEpicId() {
        return epicId;
    }
}
