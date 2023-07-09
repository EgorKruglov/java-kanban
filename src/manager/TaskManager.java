package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TaskManager {
    public Integer tickIdAndGet();

    public Integer getIdCounter();

    public void addTask(Task task);

    public void addEpic(Epic epic);

    public void addSubtask(Subtask subtask);

    public List<Task> getTasksList();

    public List<Subtask> getSubTasksList();

    public List<Epic> getEpicsList();

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    public Task getTask(Integer id);

    public Epic getEpic(Integer id);

    public Subtask getSubtask(Integer id);

    public void updateTask(Integer taskId, Task task);

    public void updateEpic(Integer epicId, Epic epic);

    public void updateSubtask(Integer subtaskId, Subtask subtask);

    public void deleteTask(Integer taskId);

    public void deleteEpic(Integer epicId);

    public void deleteSubTask(Integer subTaskId);

    public List<Subtask> getSubtasksByEpic(Integer epicId);

    public Map<Integer, Task> getTasks();

    public Map<Integer, Epic> getEpics();

    public Map<Integer, Subtask> getSubtasks();

    public List<Task> getHistory();
}
