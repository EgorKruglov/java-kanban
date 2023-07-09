package tests;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;


public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    @Override
    void freshProgramBeforeTest() {
        manager = Managers.getDefault();
    }

}
