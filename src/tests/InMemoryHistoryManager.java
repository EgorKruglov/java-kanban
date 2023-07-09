package tests;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/*HelperForTests наследуется ради удобных методов тестирования.
Хотя сохранять таски в manager для тестирования HistoryManager и не нужно*/
public class InMemoryHistoryManager extends HelperForTests<TaskManager> {

    HistoryManager historyManager;

    @BeforeEach
    void freshProgramBeforeTest() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault();
    }

    @Test
    void whenHistoryIsClear() {
        assertEquals(0, historyManager.getHistory().size());

        assertDoesNotThrow(
                new Executable() {
                    @Override
                    public void execute() {
                        historyManager.remove(2);
                    }
                });
    }

    @Test
    void add() {
        createTask1();
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());

        createEpic();
        createSubtask1();
        historyManager.add(epic);
        historyManager.add(subtask1);
        assertEquals(List.of(task1, epic, subtask1), historyManager.getHistory());
    }

    @Test
    void addDuplicate() {
        createTask1();
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void remove() {
        createTask1();
        historyManager.add(task1);
        historyManager.remove(task1Id);
        assertEquals(0, historyManager.getHistory().size());

        createTask1();
        createEpic();
        createSubtask1();
        createSubtask2();
        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.remove(2); // Удаление из начала
        assertEquals(List.of(epic,subtask1, subtask2), historyManager.getHistory());

        historyManager.remove(4); // Удаление из середины
        assertEquals(List.of(epic, subtask2), historyManager.getHistory());

        historyManager.remove(5); // Удаление с конца
        assertEquals(List.of(epic), historyManager.getHistory());

    }
}
