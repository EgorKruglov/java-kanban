public class Task {
    private final String title;
    private final String description;
    private String status;
    private final Integer id; // Id задачи

    public Task(Integer id, String title, String description) { // Создание
        this.id = id;
        this.title = title;
        this.description = description;
        status = "NEW";
    }

    public Task(Integer id, String title, String description, String status) { // Обновление
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
