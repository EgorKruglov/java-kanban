package tests;

import manager.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.KVServer;

public class HttpTaskManagerTest extends FileBackedTasksManagerTest {
    KVServer kvServer;

    @BeforeEach
    @Override
    void freshProgramBeforeTest() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            manager = new HttpTaskManager("http://localhost:8078");
        } catch (java.io.IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void closeDataServer() {
        kvServer.stop();
    }

    @Override
    public void loadToResultManager() {
        try {
            resultManager = new HttpTaskManager("http://localhost:8078");
        } catch (java.io.IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
