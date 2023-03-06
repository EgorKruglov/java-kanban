import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subTasksId;    // Эпик хранит только Id своих подзадач.

    public Epic(String title, String description) {
        super(title, description);
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
}
