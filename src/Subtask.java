public class Subtask extends Task {
    public Subtask(String title, String description) {
        super(title, description);
    }

    public Subtask(String title, String description, String status) {
        super(title, description, status);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
