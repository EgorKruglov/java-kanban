import java.util.ArrayList;

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
                ", title='" + title + '\'' +
                ", description='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    ArrayList<Integer> getSubTasksId () {
        return subTasksId;
    }

    void addSubTaskId (Integer id) {
        subTasksId.add(id);
    }

    void removeSubTaskId (Integer id) {
        subTasksId.remove(id);
    }
}
