package tests;

import manager.FileBackedTasksManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    TaskManager resultManager;

    @BeforeEach
    @Override
    void freshProgramBeforeTest() {
        manager = new FileBackedTasksManager("src\\Memory.csv");
    }

    /*Сериализация*/

    @Test
    void saveWhenNoTasks() {
        manager.save();
        loadToResultManager();
        assertEquals(List.of(), resultManager.getTasksList());
        assertEquals(List.of(), resultManager.getEpicsList());
        assertEquals(List.of(), resultManager.getSubTasksList());
    }

    @Test
    void saveWithTasks() {
        createTask1();
        loadToResultManager();
        assertEquals(List.of(task1), resultManager.getTasksList());

        createEpic();
        loadToResultManager();
        assertEquals(List.of(task1), resultManager.getTasksList());
        assertEquals(List.of(epic), resultManager.getEpicsList());

        createSubtask1();
        loadToResultManager();
        assertEquals(List.of(task1), resultManager.getTasksList());
        assertEquals(List.of(epic), resultManager.getEpicsList());
        assertEquals(List.of(subtask1), resultManager.getSubTasksList());

        createSubtask2();
        loadToResultManager();
        assertEquals(List.of(task1), resultManager.getTasksList());
        assertEquals(List.of(epic), resultManager.getEpicsList());
        assertEquals(List.of(subtask1, subtask2), resultManager.getSubTasksList());
    }

    @Test
    void saveWithHistory() {
        createTask1();
        manager.getTask(task1Id);
        loadToResultManager();
        assertEquals(List.of(task1), resultManager.getHistory());

        createEpic();
        manager.getEpic(epicId);
        loadToResultManager();
        assertEquals(List.of(task1, epic), resultManager.getHistory());

        createSubtask1();
        manager.getSubtask(subtask1Id);
        loadToResultManager();
        assertEquals(List.of(task1, epic, subtask1), resultManager.getHistory());

        createSubtask2();
        manager.getSubtask(subtask2Id);
        loadToResultManager();
        assertEquals(List.of(task1, epic, subtask1, subtask2), resultManager.getHistory());

        manager.getSubtask(subtask1Id); // Тест вызова уже созданных задач
        manager.getEpic(epicId);
        manager.addTask(task1);
        loadToResultManager();
        assertEquals(List.of(task1, epic, subtask1, subtask2), resultManager.getHistory()); // Задачи ви истории стоят по возрастания id
    }

    public void loadToResultManager() {
        resultManager = FileBackedTasksManager.loadFromFile(new File("src\\Memory.csv"));
    }
}
