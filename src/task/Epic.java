package task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subTasksId;    // Эпик хранит только Id своих подзадач.

    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        subTasksId = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasksId.size() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription().length() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getId(), subTasksId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic otherEpic = (Epic) obj;
        return Objects.equals(getTitle(), otherEpic.getTitle()) &&
                Objects.equals(getDescription(), otherEpic.getDescription()) &&
                Objects.equals(getStatus(), otherEpic.getStatus()) &&
                Objects.equals(getId(), otherEpic.getId()) &&
                getSubTasksId().equals(otherEpic.subTasksId);
    }

    public ArrayList<Integer> getSubTasksId () {return subTasksId;}

    public void addSubTaskId (Integer id) {
        subTasksId.add(id);
    }

    public void removeSubTaskId (Integer id) {
        subTasksId.remove(id);
    }
}