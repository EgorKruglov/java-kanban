import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        //TaskManager taskManager = Managers.getDefault();
        TaskManager saveManager = Managers.getAutoSave(new File("C:\\Users\\admin\\dev\\java-kanban\\src\\Memory.csv"));

        // + Две задачи
        saveManager.addTask(new Task(saveManager.tickIdAndGet(), "Погулять с детьми",
                "Давно не были на ВДНХ"));
        saveManager.addTask(new Task(saveManager.tickIdAndGet(), "Приготовить ужин",
                "Нужны лук, помидоры, картошка, говядина, сметана."));

        // + Эпик с тремя задачами
        saveManager.addEpic(new Epic(saveManager.tickIdAndGet(), "Поменять колёса",
                "На этой неделе надо успеть"));
        saveManager.addSubtask(saveManager.getIdCounter(), new Subtask(saveManager.tickIdAndGet(),
                "У Валерия насос забрать", "", saveManager.getIdCounter()-1));
        saveManager.addSubtask(saveManager.getIdCounter()-1, new Subtask(saveManager.tickIdAndGet(),
                "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.", saveManager.getIdCounter()-1));
        saveManager.addSubtask(saveManager.getIdCounter()-2, new Subtask(saveManager.tickIdAndGet(),
                "Отдать Валерию диск",
                "Давно обещал вернуть диск с фото отдыха", saveManager.getIdCounter()-2));

        // + Эпик пустой
        saveManager.addEpic(new Epic(saveManager.tickIdAndGet(),"Съездить к маме",
                "Обещал на 20 числа, но пришлось перенести."));

        // Запросы тасков
        System.out.println(saveManager.getTask(1));
        System.out.println(saveManager.getEpic(3));
        System.out.println(saveManager.getTask(1));
        System.out.println(saveManager.getTask(2));
        System.out.println(saveManager.getEpic(7));

    }
}
