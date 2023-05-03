package manager;

import task.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public void add(Task task);

    public void remove(int id);

    public ArrayList<Task> getHistory();
}
