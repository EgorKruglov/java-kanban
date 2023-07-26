package manager;

import java.io.File;
import java.io.IOException;

public class Managers {

    private Managers () {}

    public static TaskManager getDefault(String URIOfDataServer) throws IOException, InterruptedException {
        return new HttpTaskManager(URIOfDataServer);
    }

    public static TaskManager getWithoutSave() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getAutoSave(String path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
