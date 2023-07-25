package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    public Integer tickIdAndGet();

    public Integer getIdCounter();

    public Boolean addTask(Task task);

    public Boolean addEpic(Epic epic);

    public Boolean addSubtask(Subtask subtask);

    public List<Task> getTasksList();

    public List<Subtask> getSubTasksList();

    public List<Epic> getEpicsList();

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    public Task getTask(Integer id);

    public Epic getEpic(Integer id);

    public Subtask getSubtask(Integer id);

    public Boolean updateTask(Integer taskId, Task task);

    public Boolean updateEpic(Integer epicId, Epic epic);

    public Boolean updateSubtask(Integer subtaskId, Subtask subtask);

    public Boolean deleteTask(Integer taskId);

    public Boolean deleteEpic(Integer epicId);

    public Boolean deleteSubTask(Integer subTaskId);

    public List<Subtask> getSubtasksByEpic(Integer epicId);

    public Map<Integer, Task> getTasks();

    public Map<Integer, Epic> getEpics();

    public Map<Integer, Subtask> getSubtasks();

    public List<Task> getHistory();

    public TreeSet<Task> getPrioritizedTasks();
}
