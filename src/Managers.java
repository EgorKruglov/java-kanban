import java.util.ArrayList;

public class Managers {

    public Managers () {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static ArrayList<Task> getDefaultHistory() {
        return new InMemoryHistoryManager().getHistory();
    }
}
