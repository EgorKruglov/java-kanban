package tests;

import manager.HistoryManager;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;

/*Этот класс добавляет удобные методы по быстрому созданию задач, эпиков, подзадач*/
abstract class HelperForTests<T extends TaskManager> {

    Epic epic;
    Integer epicId;
    Subtask subtask1;
    Subtask subtask2;
    Integer subtask1Id;
    Integer subtask2Id;
    Task task1;
    Task task2;
    Integer task1Id;
    Integer task2Id;
    T manager;

    void createSubtask1() {
        subtask1 = new Subtask(manager.tickIdAndGet(), // + subtask
                "subtask title 1",
                "subtask description 1",
                epicId,
                45,
                LocalDateTime.of(2023, 7,10,12,0));
        manager.addSubtask(subtask1);
        subtask1Id = manager.getIdCounter();
    }

    void createSubtask2() {
        subtask2 = new Subtask(manager.tickIdAndGet(), // + subtask
                "subtask title 2",
                "subtask description 2",
                epicId,
                30,
                LocalDateTime.of(2023, 7,10,15,0));
        manager.addSubtask(subtask2);
        subtask2Id = manager.getIdCounter();
    }

    void createEpic() {
        epic = new Epic(manager.tickIdAndGet(), "epic title 1", "epic description 1"); // + epic
        manager.addEpic(epic);
        epicId = manager.getIdCounter();
    }

    void createEpicWithTwoSubtasks() {
        createEpic();
        createSubtask1();
        createSubtask2();
    }

    void createTask1() {
        task1 = new Task(manager.tickIdAndGet(),
                "task title 1",
                "task description 1",
                15,
                LocalDateTime.of(2023, 7,12,9,30));
        manager.addTask(task1);
        task1Id = task1.getId();
    }

    void createTask2() {
        task2 = new Task(manager.tickIdAndGet(), "task title 2", "task description 2");
        manager.addTask(task2);
        task2Id = task2.getId();
    }
}
