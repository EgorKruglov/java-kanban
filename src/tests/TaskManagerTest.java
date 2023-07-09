package tests;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> extends HelperForTests<T> {

    @BeforeEach
    abstract void freshProgramBeforeTest();

    /*Epics*/

    @Test
    void addEpicWithNoSubtasks() { // Для теста эпика без подзадач
        createEpic();

        assertNotNull(epicId);

        Epic epicCopy = manager.getEpic(epicId);
        assertEquals(epicCopy, epic);

        List<Epic> epicsList = manager.getEpicsList();
        assertEquals(List.of(epicCopy), epicsList);

        Map<Integer, Epic> epics = manager.getEpics();
        Map<Integer, Epic> trueResult = new HashMap<>();
        trueResult.put(epicId, epicCopy);
        assertEquals(trueResult, epics);

        manager.updateEpic(epicId, new Epic(epicId, "epic title 1", "epic description 2"));
        assertEquals(new Epic(epicId, "epic title 1", "epic description 2"), manager.getEpic(epicId));

        List<Subtask> epicsSubtasks = manager.getSubtasksByEpic(epicId);
        assertEquals(List.of(), epicsSubtasks);

        manager.deleteEpic(epicId);
        assertEquals(new HashMap<>(), manager.getEpics());

        Epic epic1 = new Epic(manager.tickIdAndGet(),"epic title 1", "epic description 1");
        Epic epic2 = new Epic(manager.tickIdAndGet(),"epic title 2", "epic description 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        assertEquals(List.of(epic1, epic2), manager.getEpicsList());

        manager.deleteAllEpics();

        assertEquals(new HashMap<>(), manager.getEpics());
    }

    @Test
    void addEpicWithSubtasks() { // Для теста эпика с подзадачами (не забыть про тест самих подзадач)
        createEpic();
        createSubtask1();

        assertEquals(List.of(subtask1), manager.getSubtasksByEpic(epicId));

        manager.deleteSubTask(subtask1Id);
        assertEquals(List.of(), manager.getSubtasksByEpic(epicId));

        manager.addSubtask(subtask1); // Вернём удалённую подзадачу
        manager.deleteAllSubtasks();
        assertEquals(List.of(), manager.getSubtasksByEpic(epicId));

        manager.addSubtask(subtask1); // Вернём удалённую подзадачу
        subtask1 = new Subtask(subtask1Id, "subtask title 1", "subtask description 2", epicId);
        manager.updateSubtask(subtask1Id, subtask1);
        assertEquals(List.of(subtask1), manager.getSubtasksByEpic(epicId));

        manager.updateEpic(epicId, new Epic(epicId, "epic title 1", "epic description 2"));
        assertEquals(List.of(subtask1Id), manager.getEpic(epicId).getSubTasksId());

        manager.deleteEpic(epicId);
        assertEquals(0, manager.getSubtasks().size());

        manager.addEpic(epic); // Вернём удалённый эпик
        manager.addSubtask(subtask1); // Вернём удалённую подзадачу
        manager.deleteAllEpics();
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void epicChangeStatus() {
        createEpic();
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus());

        createEpicWithTwoSubtasks();
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus());

        manager.updateSubtask(subtask1Id, new Subtask(subtask1Id,
                "subtask title 1",
                "subtask description 1",
                epicId,
                Status.IN_PROGRESS));
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());

        manager.updateSubtask(subtask1Id, new Subtask(subtask1Id,
                "subtask title 1",
                "subtask description 1",
                epicId,
                Status.DONE));
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());

        manager.updateSubtask(subtask2Id, new Subtask(subtask2Id,
                "subtask title 2",
                "subtask description 2",
                epicId,
                Status.DONE));
        assertEquals(Status.DONE, manager.getEpic(epicId).getStatus());

        manager.updateSubtask(subtask2Id, new Subtask(subtask2Id,
                "subtask title 2",
                "subtask description 2",
                epicId,
                Status.IN_PROGRESS));
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicId).getStatus());

        manager.deleteSubTask(subtask1Id);
        manager.deleteSubTask(subtask2Id);
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus());
    }

    /*Subtasks*/

    @Test
    void addSubtask() {
        createEpic();
        assertEquals(List.of(), manager.getSubTasksList());

        createSubtask1();
        assertNotNull(subtask1Id);
        assertEquals(List.of(subtask1), manager.getSubTasksList());

        createSubtask2();
        assertNotNull(subtask2Id);
        assertEquals(List.of(subtask1, subtask2), manager.getSubTasksList());

        manager.deleteSubTask(subtask1Id);
        assertEquals(List.of(subtask2), manager.getSubTasksList());

        manager.deleteAllSubtasks();
        assertEquals(List.of(), manager.getSubTasksList());

        createEpicWithTwoSubtasks();
        assertEquals(subtask1, manager.getSubtask(subtask1Id));

        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        subtask2 = new Subtask(manager.tickIdAndGet(),
                "subtask title 2",
                "subtask description 2",
                epicId,
                Status.IN_PROGRESS);
        manager.updateSubtask(subtask2Id, subtask2);
        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        assertEquals(subtask2, manager.getSubtask(subtask2Id));

        epic = new Epic(epicId, "epic title 1", "epic description 2");
        manager.updateEpic(epicId, epic);
        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));


        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask1Id, subtask1);
        subtasks.put(subtask2Id, subtask2);
        assertEquals(subtasks, manager.getSubtasks());

        manager.deleteEpic(epicId);
        assertEquals(List.of(), manager.getSubTasksList());
    }

    /*Tasks*/

    @Test
    void addTask() {
        final Map<Integer, Task> tasks = new HashMap<>();

        assertEquals(List.of(), manager.getTasksList());

        assertEquals(tasks, manager.getTasks());

        createTask1();

        assertNotNull(task1Id);

        assertEquals(task1, manager.getTask(task1Id));

        tasks.put(task1Id, task1);
        assertEquals(tasks, manager.getTasks());

        createTask2();
        tasks.put(task2Id, task2);
        assertEquals(tasks, manager.getTasks());

        assertEquals(List.of(task1, task2), manager.getTasksList());

        task2 = new Task(task2Id, "task title 2", "task description 3");
        manager.updateTask(task2Id, task2);
        assertEquals(task2, manager.getTask(task2Id));

        manager.deleteTask(task1Id);
        assertEquals(List.of(task2), manager.getTasksList());

        manager.deleteAllTasks();
        assertEquals(List.of(), manager.getTasksList());
    }
}
