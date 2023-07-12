package task;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private LocalDateTime endTime;

    private List<Integer> subTasksId;    // Эпик хранит только Id своих подзадач.



    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        subTasksId = new ArrayList<>();
    }
    /*Ещё один конструктор не нужен так как при сериализации эпик расчитывается сам*/
/*    public Epic(Integer id, String title, String description, Status status, Integer duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, title, description, status, duration, startTime);
        this.endTime = endTime;
    }*/

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                ", subTasks=" + subTasksId.size() +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getId(), subTasksId, getStartTime(), getDuration());
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
                getSubTasksId().equals(otherEpic.subTasksId) &&
                Objects.equals(getStartTime(), otherEpic.getStartTime()) &&
                Objects.equals(getDuration(), otherEpic.getDuration());
    }

    public List<Integer> getSubTasksId() {return subTasksId;}

    public void setSubTasksId(List<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }

    public void addSubTaskId (Integer id) {
        subTasksId.add(id);
    }

    public void removeSubTaskId (Integer id) {
        subTasksId.remove(id);
    }

    public void clearSubTasks() {
        subTasksId.clear();
    }

    public void updateEpic(Map<Integer, Subtask> subtasks) {
        updateStatus(subtasks);

        if (subTasksId.isEmpty()) {
            setStartTime(null);
            setDuration(null);
            endTime = null;
            return;
        }

        updateStartTime(subtasks);
        updateDuration(subtasks);
        updateEndTime(subtasks);
    }

    private void updateStatus(Map<Integer, Subtask> subtasks) { // Изменяет статус эпика
    /* Если есть хоть одна подзадача "IN_PROGRESS", значит статус эпика "IN_PROGRESS". Если не будет ни одной, значит
    возможно три варианта:
    * Все "DONE"
    * Все "NEW"
    * Есть и "DONE" и "NEW".
    * С помощью флагов и условий можно это эффективно проверить и найти статус эпика. */

        if (getSubTasksId().size() == 0) {
            setStatus(Status.NEW); // Если передали эпик без подзадач
            return;
        }

        boolean isDoneContains = false;
        boolean isNewContains = false;
        for (Integer subtaskID : getSubTasksId()) {
            if (subtasks.get(subtaskID).getStatus().equals(Status.IN_PROGRESS)) {
                setStatus(Status.IN_PROGRESS);
                return;
            }
            // Если уже найдена, то не проверять. // Ищет "NEW" подзадачу
            if (!(isNewContains) && subtasks.get(subtaskID).getStatus().equals(Status.NEW)) isNewContains = true;
            // Ищет "DONE" подзадачу
            if (!(isDoneContains) && subtasks.get(subtaskID).getStatus().equals(Status.DONE)) isDoneContains = true;
        }
        if (isNewContains) {
            if (isDoneContains) {
                setStatus(Status.IN_PROGRESS);
                return;
            } else {
                setStatus(Status.NEW);
                return;
            }
        }
        if (isDoneContains) {
            setStatus(Status.DONE);
        }
    }

    private void updateDuration(Map<Integer, Subtask> subtasks) { // Продолжительность эпика это сумма п. подзадач
        Integer resultDuration = 0;
        for (Integer subtaskID : getSubTasksId()) {
            Subtask subtask = subtasks.get(subtaskID);
            if (subtask.getDuration() != null) {
                resultDuration += subtasks.get(subtaskID).getDuration();
            }
        }
        setDuration(resultDuration);
    }

    private void updateStartTime(Map<Integer, Subtask> subtasks) {

        for (Integer subtaskID : getSubTasksId()) { // Присваиваем рандомное startTime эпику из его подзадач
            if (subTasksId.contains(subtaskID)) {
                LocalDateTime subtaskStartTime = subtasks.get(subtaskID).getStartTime();
                if (subtaskStartTime != null) {
                    setStartTime(subtaskStartTime);
                    break;
                }
            }
        }


        for (Integer subtaskID : getSubTasksId()) {
            if (subTasksId.contains(subtaskID)) { // Смотрим только принадлежащие этому эпику подзадачи
                Subtask subtask = subtasks.get(subtaskID);
                if (subtask.getStartTime() != null) {
                    LocalDateTime subtaskStartTime = subtask.getStartTime();
                    if (getStartTime().isAfter(subtaskStartTime)) { // Находим самое раннее startTime у подзадач
                        setStartTime(subtaskStartTime);
                    }
                }
            }
        }
    }

    private void updateEndTime(Map<Integer, Subtask> subtasks) {
        /*Поскольку пользователь выполняет только одну задачу за раз, endTime эпика
        * это endTime самой поздней его подзадачи*/

        LocalDateTime someSubtaskStartTime = null;
        LocalDateTime iterateSubtaskStartTime = null;
        Integer iterateSubtaskDuration = null;

        for (Integer subtaskID : getSubTasksId()) { // Присваиваем рандомное startTime эпику из его подзадач
            if (subTasksId.contains(subtaskID)) {
                iterateSubtaskStartTime = subtasks.get(subtaskID).getStartTime();
                if (iterateSubtaskStartTime != null) {
                    someSubtaskStartTime = iterateSubtaskStartTime;
                    break;
                }
            }
        }

        if (someSubtaskStartTime == null) { // Если у всех подзадач нет startTime
            return;
        }

        for (Integer subtaskID : getSubTasksId()) {
            if (subTasksId.contains(subtaskID)) { // Смотрим только принадлежащие этому эпику подзадачи
                iterateSubtaskStartTime = subtasks.get(subtaskID).getStartTime();
                iterateSubtaskDuration = subtasks.get(subtaskID).getDuration();
                if (iterateSubtaskStartTime != null) {
                    if (someSubtaskStartTime.isBefore(iterateSubtaskStartTime)) { // Находим самое позднее startTime у подзадач
                        someSubtaskStartTime = iterateSubtaskStartTime;
                    }
                }
            }
        }

        if (iterateSubtaskDuration == null) { // Если у всех подзадач нет startTime
            return;
        }

        endTime = someSubtaskStartTime.plusMinutes(iterateSubtaskDuration);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
