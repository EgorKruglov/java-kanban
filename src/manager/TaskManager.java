package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    public Integer tickIdAndGet();

    public Integer getIdCounter();

    public void addTask(Task task);

    public void addEpic(Epic epic);

    public void addSubtask(Integer epicId, Subtask subtask);

    public ArrayList<Task> getTasksList();

    public ArrayList<Subtask> getSubTasksList();

    public ArrayList<Epic> getEpicsList();

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

    public ArrayList<Subtask> getSubtasksByEpic(Integer epicId);

    public HashMap<Integer, Task> getTasks();

    public HashMap<Integer, Epic> getEpics();

    public HashMap<Integer, Subtask> getSubtasks();

    public ArrayList<Task> getHistory();
}
