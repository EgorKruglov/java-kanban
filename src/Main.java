import manager.Managers;
import manager.TaskManager;
import server.KVServer;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        new KVServer().start();
        TaskManager manager = Managers.getDefault("http://localhost:8078");

        // + Две задачи
        manager.addTask(new Task(0,
                "Погулять с детьми",
                "Давно не были на ВДНХ"));

        manager.addTask(new Task(1,
                "Приготовить ужин",
                "Нужны лук, помидоры, картошка, говядина, сметана."));

        // + Эпик с тремя задачами
        manager.addEpic(new Epic(2,
                "Поменять колёса",
                "На этой неделе надо успеть"));

        manager.addSubtask(new Subtask(3,
                "У Валерия насос забрать",
                "",
                2,
                Status.NEW,
                30,
                LocalDateTime.of(2023,7,20,12,0)));

        manager.addSubtask(new Subtask(4,
                "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.",
                2,
                Status.NEW,
                500,
                LocalDateTime.of(2023,7,21,12,0)));

        manager.addSubtask(new Subtask(5,
                "Отдать Валерию диск",
                "Давно обещал вернуть диск с фото отдыха",
                2,
                Status.IN_PROGRESS,
                30,
                LocalDateTime.of(2023,7,19,12,0)));

        // + Эпик пустой
        manager.addEpic(new Epic(6,
                "Съездить к маме",
                "Обещал на 20 числа, но пришлось перенести."));

        // Запросы тасков
        manager.getTask(0);
        manager.getEpic(2);
        manager.getTask(0);
        manager.getSubtask(3);
        manager.getEpic(2);
        manager.getEpic(6);

        TaskManager testManager = Managers.getDefault("http://localhost:8078");
        // Выведем прочитанные данные
        System.out.println("\n" + testManager.getTasksList());
        System.out.println(testManager.getEpicsList());
        System.out.println(testManager.getSubTasksList());
        System.out.println(testManager.getHistory());
    }
}
