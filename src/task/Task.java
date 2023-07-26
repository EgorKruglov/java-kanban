package task;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String title;
    private final String description;
    private Status status;
    private final Integer id; // Id задачи
    private LocalDateTime startTime; // Момент начала выполнения
    private Integer duration; // Продолжительность в минутах

    public Task(Integer id, String title, String description) { // Создание
        this.id = id;
        this.title = title;
        this.description = description;
        status = Status.NEW;
    }

    public Task(Integer id, String title, String description, Status status) { // Обновление
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String title, String description, Integer duration, LocalDateTime startTime){
        this.id = id;
        this.title = title;
        this.description = description;
        status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Integer id, String title, String description, Status status, Integer duration, LocalDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\''+
                ", status='" + status + '\''+
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id, startTime, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(title, otherTask.title) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(id, otherTask.id) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(duration, otherTask.duration);
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plusMinutes(duration);
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
