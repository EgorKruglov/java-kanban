package tests.util;

import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;

/*Этот класс добавляет удобные методы по быстрому созданию задач, эпиков, подзадач*/
public abstract class HelperForTests<T extends TaskManager> {

    public Epic epic;
    public Integer epicId;
    public Subtask subtask1;
    public Subtask subtask2;
    public Integer subtask1Id;
    public Integer subtask2Id;
    public Task task1;
    public Task task2;
    public Integer task1Id;
    public Integer task2Id;
    public T manager;

    public void createSubtask1() {
        subtask1 = new Subtask(manager.tickIdAndGet(), // + subtask
                "subtask title 1",
                "subtask description 1",
                epicId,
                45,
                LocalDateTime.of(2023, 7,10,12,0));
        manager.addSubtask(subtask1);
        subtask1Id = manager.getIdCounter();
    }

    public void createSubtask2() {
        subtask2 = new Subtask(manager.tickIdAndGet(), // + subtask
                "subtask title 2",
                "subtask description 2",
                epicId,
                30,
                LocalDateTime.of(2023, 7,10,15,0));
        manager.addSubtask(subtask2);
        subtask2Id = manager.getIdCounter();
    }

    public void createEpic() {
        epic = new Epic(manager.tickIdAndGet(), "epic title 1", "epic description 1"); // + epic
        manager.addEpic(epic);
        epicId = manager.getIdCounter();
    }

    public void createEpicWithTwoSubtasks() {
        createEpic();
        createSubtask1();
        createSubtask2();
    }

    public void createTask1() {
        task1 = new Task(manager.tickIdAndGet(),
                "task title 1",
                "task description 1",
                15,
                LocalDateTime.of(2023, 7,12,9,30));
        manager.addTask(task1);
        task1Id = task1.getId();
    }

    public void createTask2() {
        task2 = new Task(manager.tickIdAndGet(), "task title 2", "task description 2");
        manager.addTask(task2);
        task2Id = task2.getId();
    }
}
