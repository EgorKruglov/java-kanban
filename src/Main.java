import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // + Две задачи
        taskManager.addTask(new Task(taskManager.tickIdAndGet(), "Погулять с детьми",
                "Давно не были на ВДНХ"));
        taskManager.addTask(new Task(taskManager.tickIdAndGet(), "Приготовить ужин",
                "Нужны лук, помидоры, картошка, говядина, сметана"));

        // + Эпик с тремя задачами
        taskManager.addEpic(new Epic(taskManager.tickIdAndGet(), "Поменять колёса",
                "На этой неделе надо успеть"));
        taskManager.addSubtask(taskManager.getIdCounter(), new Subtask(taskManager.tickIdAndGet(),
                "У Валерия насос забрать", "", taskManager.getIdCounter()));
        taskManager.addSubtask(taskManager.getIdCounter()-1, new Subtask(taskManager.tickIdAndGet(),
                "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.", taskManager.getIdCounter()-1));
        taskManager.addSubtask(taskManager.getIdCounter()-2, new Subtask(taskManager.tickIdAndGet(),
                "Отдать Валерию диск",
                "Давно обещал вернуть диск с фото отдыха", taskManager.getIdCounter()-2));

        // + Эпик пустой
        taskManager.addEpic(new Epic(taskManager.tickIdAndGet(),"Съездить к маме",
                "Обещал на 20 числа, но пришлось перенести"));

        // Напечатать всё
        System.out.println("\n"+taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println("\n"+taskManager.getTask(1)); // Получаю таски
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(2));

        System.out.println("\nИстория:");
        System.out.println(taskManager.getHistory()); // Получаю историю

        System.out.println("\n"+taskManager.getEpic(7)); // Получаю таски
        System.out.println(taskManager.getTask(1));

        System.out.println("\nИстория:");
        System.out.println(taskManager.getHistory()); // Получаю историю

        // Удаляю задачи
        taskManager.deleteTask(1);
        System.out.println("\nИстория:");
        System.out.println(taskManager.getHistory()); // Получаю историю

        taskManager.deleteTask(3);
        System.out.println("\nИстория:");
        System.out.println(taskManager.getHistory()); // Получаю историю

    }
}
