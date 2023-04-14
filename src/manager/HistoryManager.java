package manager;

import org.w3c.dom.Node;
import task.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public void add(Task task);

    public void remove(int id);

    public ArrayList getHistory();
}
