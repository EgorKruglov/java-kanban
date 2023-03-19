import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() > 9) history.remove(0);
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
