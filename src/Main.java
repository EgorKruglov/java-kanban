public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        // + две задачи
        taskManager.addTask(new Task(taskManager.tickIdAndGet(), "Погулять с детьми",
                "Давно не были на ВДНХ"));
        taskManager.addTask(new Task(taskManager.tickIdAndGet(), "Приготовить ужин",
                "Нужны лук, помидоры, картошка, говядина, сметана"));

        // + эпик с двумя задачами
        taskManager.addEpic(new Epic(taskManager.tickIdAndGet(), "Поменять колёса",
                "На этой неделе надо успеть"));
        taskManager.addSubtask(taskManager.getIdCounter(), new Subtask(taskManager.tickIdAndGet(),
                "У Валерия насос забрать", "", taskManager.getIdCounter()));
        taskManager.addSubtask(taskManager.getIdCounter()-1, new Subtask(taskManager.tickIdAndGet(),
                "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.", taskManager.getIdCounter()-1));

        // + эпик с одной подзадачей
        taskManager.addEpic(new Epic(taskManager.tickIdAndGet(),"Съездить к маме",
                "Обещал на 20 числа, но пришлось перенести"));
        taskManager.addSubtask(taskManager.getIdCounter(), new Subtask(taskManager.tickIdAndGet(),
                "Купить банки для огурцов",
                "Посмотреть в новом магазине", taskManager.getIdCounter()));

        System.out.println("\n"+taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        // Меняю статусы
        taskManager.updateTask(1, new Task(1, "Гуляю", "С детьми на ВДНХ", Statuses.IN_PROGRESS));
        taskManager.updateSubtask(5, new Subtask(5, "Визга нет", "Попал камешек",
                3, Statuses.DONE));
        taskManager.updateSubtask(7, new Subtask(7, "Купить банки для огурцов", "", 6,
                Statuses.DONE));

        System.out.println("\n"+taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.deleteTask(2);  //Удаляю задачу
        taskManager.deleteEpic(6);  //Удаляю эпик

        System.out.println("\n"+taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println("\n"+taskManager.getTask(1));
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getSubtask(5));
        System.out.println(taskManager.getHistory());
    }
}
