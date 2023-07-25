package tests;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> extends HelperForTests<T> {

    @BeforeEach
    abstract void freshProgramBeforeTest();

    /*Тест сортировки задач*/

    @Test
    void prioritizedTasksTest() {
        assertEquals(Set.of(), manager.getPrioritizedTasks());

        Task task1 = new Task(1,
                "task title 1",
                "task description 1",
                15,
                LocalDateTime.of(2023, 7,12,9,30));

        manager.addTask(task1);
        assertEquals(Set.of(task1), manager.getPrioritizedTasks());

        task1 = new Task(1,
                "task title 1",
                "task description 1",
                Status.IN_PROGRESS,
                20,
                LocalDateTime.of(2013, 5,12,9,30));
        manager.updateTask(1, task1);

        assertEquals(Set.of(task1), manager.getPrioritizedTasks());

        Task task2 = new Task(2,
                "task title 1",
                "task description 1",
                15,
                LocalDateTime.of(2023, 7,12,9,30));

        manager.addTask(task2);
        assertEquals(Set.of(task1, task2), manager.getPrioritizedTasks());

        createEpicWithTwoSubtasks();
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Set.of(task1, task2, subtask1, subtask2), manager.getPrioritizedTasks());

        manager.deleteAllTasks();
        assertEquals(Set.of(subtask1, subtask2), manager.getPrioritizedTasks());

        manager.deleteAllEpics();
        assertEquals(Set.of(), manager.getPrioritizedTasks());
    }

    /*Epics*/

    @Test
    void addEpicWithNoSubtasks() { // Для теста эпика без подзадач
        createEpic();

        assertNotNull(epicId);

        Epic epicCopy = manager.getEpic(epicId);
        assertEquals(epicCopy, epic);

        assertNull(manager.getEpic(epicId).getStartTime());
        assertNull(manager.getEpic(epicId).getDuration());
        assertNull(manager.getEpic(epicId).getEndTime());

        List<Epic> epicsList = manager.getEpicsList();
        assertEquals(List.of(epicCopy), epicsList);

        Map<Integer, Epic> epics = manager.getEpics();
        Map<Integer, Epic> trueResult = new HashMap<>();
        trueResult.put(epicId, epicCopy);
        assertEquals(trueResult, epics);

        List<Subtask> epicsSubtasks = manager.getSubtasksByEpic(epicId);
        assertEquals(List.of(), epicsSubtasks);


    }

    @Test
    void updateEpicWithNoSubtasks() {
        createEpic();

        Epic epicCopy = new Epic(epicId, "epic title 1", "epic description 2");
        manager.updateEpic(epicId, epicCopy);
        assertEquals(epicCopy, manager.getEpic(epicId));
        assertNull(manager.getEpic(epicId).getStartTime());
        assertNull(manager.getEpic(epicId).getDuration());
        assertNull(manager.getEpic(epicId).getEndTime());
    }

    @Test
    void deleteEpicWithNoSubtasks() {
        createEpic();

        manager.deleteEpic(epicId);
        assertEquals(new HashMap<>(), manager.getEpics());

        Epic epic1 = new Epic(epicId,"epic title 1", "epic description 1");
        Epic epic2 = new Epic(epicId+1,"epic title 2", "epic description 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        assertEquals(List.of(epic1, epic2), manager.getEpicsList());

        HashMap<Integer, Epic> epics = new HashMap<>();
        epics.put(epic1.getId(), epic1);
        epics.put(epic2.getId(), epic2);
        assertEquals(epics, manager.getEpics());

        manager.deleteAllEpics();
        assertEquals(new HashMap<>(), manager.getEpics());
    }

    @Test
    void addEpicWithSubtasks() { // Для теста эпика с подзадачами (не забыть про тест самих подзадач)
        createEpic();
        createSubtask1();

        assertEquals(List.of(subtask1), manager.getSubtasksByEpic(epicId));

        assertEquals(subtask1.getStartTime(), manager.getEpic(epicId).getStartTime());
        assertEquals(subtask1.getDuration(), manager.getEpic(epicId).getDuration());
        assertEquals(subtask1.getEndTime(), manager.getEpic(epicId).getEndTime());

        createSubtask2();
        assertEquals(subtask1.getStartTime(), manager.getEpic(epicId).getStartTime());

        Integer resultDuration = subtask1.getDuration() + subtask2.getDuration();
        assertEquals(resultDuration, manager.getEpic(epicId).getDuration());

        LocalDateTime resultEndTime = subtask2.getStartTime().plusMinutes(subtask2.getDuration());
        assertEquals(resultEndTime, manager.getEpic(epicId).getEndTime());
    }

    @Test
    void updateEpicWithSubtasks() {
        createEpic();
        createSubtask1();

        Epic newEpic = new Epic(epicId, "epic title 1", "epic description 2");
        manager.updateEpic(epicId, newEpic);
        assertEquals(newEpic, manager.getEpic(epicId));
        assertEquals(List.of(subtask1Id), manager.getEpic(epicId).getSubTasksId());

        createSubtask2();

        Integer resultDuration = subtask1.getDuration() + subtask2.getDuration();
        LocalDateTime resultEndTime = subtask2.getStartTime().plusMinutes(subtask2.getDuration());

        subtask1 = new Subtask(subtask1Id,
                "subtask title 1",
                "subtask description 2",
                epicId,
                400,
                LocalDateTime.of(2020, 1, 1, 12,9));
        manager.updateSubtask(subtask1Id, subtask1);
        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        resultDuration = subtask1.getDuration() + subtask2.getDuration();
        assertEquals(resultDuration, manager.getEpic(epicId).getDuration());
        resultEndTime = subtask2.getStartTime().plusMinutes(subtask2.getDuration());
        assertEquals(resultEndTime, manager.getEpicsList().get(0).getEndTime());
    }

    @Test
    void deleteEpicWithSubtasks() {
        createEpic();
        createSubtask1();

        manager.deleteEpic(epicId);
        assertEquals(0, manager.getSubtasks().size());
        assertEquals(0, manager.getEpics().size());

        createEpic();
        createSubtask1();

        manager.deleteSubTask(subtask1Id);
        assertEquals(List.of(), manager.getSubtasksByEpic(epicId));

        manager.addSubtask(subtask1);
        manager.deleteAllSubtasks();
        assertEquals(List.of(), manager.getSubtasksByEpic(epicId));

        createEpic();
        createSubtask1();
        manager.deleteAllEpics();
        assertEquals(0, manager.getSubtasks().size());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void epicChangeStatus() {
        createEpic();
        assertEquals(Status.NEW, manager.getEpic(epicId).getStatus());

        createSubtask1();
        createSubtask2();
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

        createTask1();
        assertEquals(subtask1, manager.getSubtask(subtask1Id));

        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        HashMap<Integer, Subtask> resultSubtasks = new HashMap<>();
        resultSubtasks.put(subtask1Id, subtask1);
        resultSubtasks.put(subtask2Id, subtask2);
        assertEquals(resultSubtasks, manager.getSubtasks());
    }

    @Test
    void updateSubtask() {
        createEpicWithTwoSubtasks();

        subtask2 = new Subtask(subtask2Id,
                "subtask title 2",
                "subtask description 3",
                epicId,
                Status.IN_PROGRESS);
        manager.updateSubtask(subtask2Id, subtask2);

        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        assertEquals(subtask2, manager.getSubtask(subtask2Id));

        LocalDateTime timeStart = LocalDateTime.of(2023, 7, 10, 15, 30);
        subtask2 = new Subtask(subtask2Id,
                "subtask title 2",
                "subtask description 4",
                epicId,
                75,
                timeStart);
        manager.updateSubtask(subtask2Id, subtask2);
        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        subtask2 = new Subtask(subtask2Id,
                "subtask title 2",
                "subtask description 4",
                epicId,
                Status.DONE,
                75,
                timeStart);
        manager.updateSubtask(subtask2Id, subtask2);
        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));

        epic = new Epic(epicId, "epic title 1", "epic description 2");
        manager.updateEpic(epicId, epic);
        assertEquals(List.of(subtask1, subtask2), manager.getSubtasksByEpic(epicId));
    }

    @Test
    void deleteSubtask() {
        createEpic();
        createSubtask1();

        manager.deleteSubTask(subtask1Id);
        assertEquals(List.of(), manager.getSubtasksByEpic(epicId));

        createSubtask1();
        manager.deleteAllSubtasks();
        assertEquals(List.of(), manager.getSubtasksByEpic(epicId));

        createSubtask1();
        createSubtask2();
        manager.deleteSubTask(subtask2Id);
        assertEquals(List.of(subtask1), manager.getSubtasksByEpic(epicId));

        manager.deleteAllSubtasks();
        assertEquals(List.of(), manager.getSubTasksList());

        manager.deleteEpic(epicId);
        assertEquals(List.of(), manager.getSubTasksList());
    }

    /*Tasks*/

    @Test
    void addTask() {
        assertEquals(List.of(), manager.getTasksList());

        final Map<Integer, Task> tasks = new HashMap<>();
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
    }

    @Test
    void updateTask() {
        createTask1();
        createTask2();

        manager.updateTask(task2Id, task2);
        assertEquals(task2, manager.getTask(task2Id));

        task2 = new Task(task2Id, "task title 2", "task description 3", Status.IN_PROGRESS);
        manager.updateTask(task2Id, task2);
        assertEquals(task2, manager.getTask(task2Id));

        task2 = new Task(task2Id, "task title 2", "task description 3", Status.DONE);
        manager.updateTask(task2Id, task2);
        assertEquals(task2, manager.getTask(task2Id));

        LocalDateTime timeStart = LocalDateTime.of(2023, 7, 10, 15, 30);
        task2 = new Task(task2Id, "task title 2", "task description 3", 90, timeStart);
        manager.updateTask(task2Id, task2);
        assertEquals(task2, manager.getTask(task2Id));
    }

    @Test
    void deleteTask() {
        createTask1();
        createTask2();

        manager.deleteTask(task1Id);
        assertEquals(List.of(task2), manager.getTasksList());

        manager.deleteAllTasks();
        assertEquals(List.of(), manager.getTasksList());
    }
}
