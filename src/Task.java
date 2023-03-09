public class Task {
    String title;
    String description;
    String status;
    private Integer id; // Id задачи

    public Task(int id, String title, String description) { // Создание
        this.id = id;
        this.title = title;
        this.description = description;
        status = "NEW";
    }

    public Task(int id, String title, String description, String status) { // Обновление
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
}
