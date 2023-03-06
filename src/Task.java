public class Task {
    String title;
    String description;
    String status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = "NEW";
    }

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
