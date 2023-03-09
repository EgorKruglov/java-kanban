public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // + две задачи
        taskManager.addTask(new Task(taskManager.TickIdAndGet(), "Погулять с детьми",
                "Давно не были на ВДНХ"));
        taskManager.addTask(new Task(taskManager.TickIdAndGet(), "Приготовить ужин",
                "Нужны лук, помидоры, картошка, говядина, сметана"));

        // + эпик с двумя задачами
        taskManager.addEpic(new Epic(taskManager.TickIdAndGet(), "Поменять колёса",
                "На этой неделе надо успеть"));
        taskManager.addSubtask(taskManager.idCounter, new Subtask(taskManager.TickIdAndGet(),
                "У Валерия насос забрать", "", taskManager.idCounter));
        taskManager.addSubtask(taskManager.idCounter-1, new Subtask(taskManager.TickIdAndGet(),
                "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.", taskManager.idCounter-1));

        // + эпик с одной подзадачей
        taskManager.addEpic(new Epic(taskManager.TickIdAndGet(),"Съездить к маме",
                "Обещал на 20 числа, но пришлось перенести"));
        taskManager.addSubtask(taskManager.idCounter, new Subtask(taskManager.TickIdAndGet(),
                "Купить банки для огурцов",
                "Посмотреть в новом магазине", taskManager.idCounter));

        System.out.println(taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);

        // Меняю статусы
        taskManager.updateTask(1, new Task(1, "Гуляю", "С детьми на ВДНХ", "IN_PROGRESS"));
        taskManager.updateSubtask(5, new Subtask(5, "Визга нет", "Попал камешек",
                taskManager.idCounter-2, "DONE"));
        taskManager.updateSubtask(7, new Subtask(7, "Купить банки для огурцов", "",
                taskManager.idCounter, "DONE"));

        System.out.println("\n"+taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);

        //Удаляю задачу
        taskManager.deleteTask(2);
        //Удаляю эпик
        taskManager.deleteEpic(6);

        System.out.println("\n"+taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);
    }
}
