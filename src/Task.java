public class Task {
    private final String title;
    private final String description;
    private Statuses status;
    private final Integer id; // Id задачи

    public Task(Integer id, String title, String description) { // Создание
        this.id = id;
        this.title = title;
        this.description = description;
        status = Statuses.NEW;
    }

    public Task(Integer id, String title, String description, Statuses status) { // Обновление
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

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }
}
