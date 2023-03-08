public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // + две задачи
        taskManager.addTask("Погулять с детьми", "Давно не были на ВДНХ");
        taskManager.addTask("Приготовить ужин", "Нужны лук, помидоры, картошка, говядина, сметана");

        // + эпик с двумя задачами
        taskManager.addEpic("Поменять колёса", "На этой неделе надо успеть");
        taskManager.addSubtask(taskManager.idCounter, "У Валерия насос забрать", "");
        taskManager.addSubtask(taskManager.idCounter-1, "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.");

        // + эпик с одной подзадачей
        taskManager.addEpic("Съездить к маме", "Обещал на 20 числа, но пришлось перенести");
        taskManager.addSubtask(taskManager.idCounter, "Купить банки для огурцов",
                "Посмотреть в новом магазине");

        System.out.println(taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);

        // Меняю статусы
        taskManager.updateTask(1, "Гуляю", "С детьми на ВДНХ", "IN_PROGRESS");
        taskManager.updateSubtask(5, "Визга нет", "Попал камешек", "DONE");
        taskManager.updateSubtask(7, "К маме съездил и банки купил", "", "DONE");

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
