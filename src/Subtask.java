public class Subtask extends Task {

    private Integer epicId;     // Id эпика, к которому относится подзадача

    public Subtask(Integer id, String title, String description, Integer epicId) {
        super(id, title, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, String description, Integer epicId, String status) {
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

    public Integer getEpicId() {
        return epicId;
    }
}
