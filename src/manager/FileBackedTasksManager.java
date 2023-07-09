package manager;                    // Класс для сериализации
import extraExceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    static public void main(String[] args) {  // Метод для проверки сериализации
//        TaskManager saveManager = Managers.getAutoSave(new File("C:\\Users\\admin\\dev\\java-kanban\\src\\Memory.csv"));
        TaskManager saveManager = Managers.getAutoSave(new File("src\\Memory.csv"));

        // + Две задачи
        saveManager.addTask(new Task(saveManager.tickIdAndGet(), "Погулять с детьми",
                "Давно не были на ВДНХ"));
        saveManager.addTask(new Task(saveManager.tickIdAndGet(), "Приготовить ужин",
                "Нужны лук, помидоры, картошка, говядина, сметана."));

        // + Эпик с тремя задачами
        saveManager.addEpic(new Epic(saveManager.tickIdAndGet(), "Поменять колёса",
                "На этой неделе надо успеть"));
        saveManager.addSubtask(new Subtask(saveManager.tickIdAndGet(),
                "У Валерия насос забрать", "", saveManager.getIdCounter()-1));
        saveManager.addSubtask(new Subtask(saveManager.tickIdAndGet(),
                "Валерий посмотрит визг потом",
                "Какой-то визг из под колес то появляется, то пропадает.", saveManager.getIdCounter()-2));
        saveManager.addSubtask(new Subtask(saveManager.tickIdAndGet(),
                "Отдать Валерию диск",
                "Давно обещал вернуть диск с фото отдыха", saveManager.getIdCounter()-3));

        // + Эпик пустой
        saveManager.addEpic(new Epic(saveManager.tickIdAndGet(),"Съездить к маме",
                "Обещал на 20 числа, но пришлось перенести."));

        // Запросы тасков
        saveManager.getTask(1);
        saveManager.getEpic(3);
        saveManager.getTask(1);
        saveManager.getTask(2);
        saveManager.getEpic(7);

        //FileBackedTasksManager backTester = loadFromFile(new File("src\\Memory.csv"));
        /*Когда восстанавливаешь данные из файла, их не нужно сразу записывать, поэтому нужно использовать
        * методы без save()*/
        InMemoryTaskManager backTester = loadFromFile(new File("src\\Memory.csv"));
        // Выведем прочитанные данные
        System.out.println("\n" + backTester.getTasksList());
        System.out.println(backTester.getEpicsList());
        System.out.println(backTester.getSubTasksList());
        System.out.println(backTester.getHistory());
    }

    public void save() { // Сохраняет текущее состояние менеджера в файл
        try (Writer fileWriter = new FileWriter(file)) {

            fileWriter.write("\nid;type;name;status;description;epic\n"); // Напишем первую строку файла csv

            List<Task> tasks = super.getTasksList();
            List<Subtask> subtasks = super.getSubTasksList();
            List<Epic> epics = super.getEpicsList();

            for (Task task : tasks) { // Добавляем созданные задачи в файл
                fileWriter.write(String.format("%s;%s;%s;%s;%s;\n", task.getId(), TaskName.TASK, task.getTitle(),
                        task.getStatus(), task.getDescription()));
            }

            for (Epic epic : epics) {
                fileWriter.write(String.format("%s;%s;%s;%s;%s;\n", epic.getId(), TaskName.EPIC, epic.getTitle(),
                        epic.getStatus(), epic.getDescription()));
            }

            for (Subtask subtask : subtasks) {
                fileWriter.write(String.format("%s;%s;%s;%s;%s;%s;\n", subtask.getId(), TaskName.SUBTASK,
                        subtask.getTitle(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId()));
            }

            fileWriter.write("\n");

            fileWriter.write(historyToString(super.getHistory()));  // Добавить в файл историю

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные");
        }
    }

    private static String historyToString(List<Task> history) {  // Преобразовать историю вызовов в строку
        StringJoiner joiner = new StringJoiner(";");
        for (Task task : history) {
            joiner.add(String.valueOf(task.getId()));
        }
        return joiner.toString();
    }

    private static Integer[] historyFromString(String value) {  // Получить id задач из истории в файле
        String[] strValues = value.split(";") ;  // Распарсить строку и изменим тип элементов на Integer
        Integer[] intValues = new Integer[strValues.length];

        for (int i = 0; i < strValues.length; i++) {  // Из String[] сделали Integer[]
            intValues[i] = Integer.parseInt(strValues[i]);
        }
        return intValues;
    }

    public static InMemoryTaskManager loadFromFile(File file) {

        //FileBackedTasksManager backTester = new FileBackedTasksManager(file);
        InMemoryTaskManager backTester = new InMemoryTaskManager();

        int idCounter = 0;  // Счётчик-идентификатор для задач

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int lastLineId;
            if (Objects.equals(lines.get(lines.size() - 1), "")) { // Если истории нет, то прочитать на одну строку больше
                lastLineId = lines.size() - 1;
            } else {
                lastLineId = lines.size() - 2;
            }

            for (int i = 2; i < lastLineId; i++) { // В первых и последних двух сроках нет задач

                String[] lineValues = lines.get(i).split(";"); // Парсим по строке из файла
                Integer taskId = Integer.parseInt(lineValues[0]);

                if (idCounter < taskId) {  // Находим актуальный, наибольший id в файле
                    idCounter = taskId;
                }

                switch (TaskName.valueOf(lineValues[1])) {  // Добавляем таски в их хештаблицы
                    case TASK:
                        backTester.addTask(new Task(taskId,
                                lineValues[2],
                                lineValues[4],
                                Status.valueOf(lineValues[3])));
                        break;
                    case EPIC:
                        backTester.addEpic(new Epic(taskId,
                                lineValues[2],
                                lineValues[4]));
                        break;
                    case SUBTASK:
                        backTester.addSubtask(new Subtask(taskId,
                                lineValues[2],
                                lineValues[4],
                                Integer.parseInt(lineValues[5]),
                                Status.valueOf(lineValues[3])));
                        break;
                    default:
                        throw new ManagerSaveException("Не удалось загрузить часть данных");
                }
            }

            // Создаём historyManager
            String historyCharVariables = lines.get(lines.size()-1);
            if (!Objects.equals(historyCharVariables, "")) {
                Integer[] historyValues = historyFromString(historyCharVariables);
                for (Integer history : historyValues) {
                    if (backTester.getTasks().containsKey(history)) {
                        backTester.addInHistory(backTester.getTask(history));
                    } else if (backTester.getSubtasks().containsKey(history)) {
                        backTester.addInHistory(backTester.getSubtask(history));
                    } else if (backTester.getEpics().containsKey(history)) {
                        backTester.addInHistory(backTester.getEpic(history));
                    }
                }
            }

        } catch (IOException ex) {
            throw new ManagerSaveException("Не удалось загрузить данные");
        }
        return backTester;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTask(Integer taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(Integer epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public void updateTask(Integer taskId, Task task) { // Обновление задачи
        super.updateTask(taskId, task);
        save();
    }

    @Override
    public void updateEpic(Integer epicId, Epic epic) { // Обновление эпика
        super.updateEpic(epicId, epic);
        save();
    }

    @Override
    public void updateSubtask(Integer subtaskId, Subtask subtask) { // Обновление подзадачи
        super.updateSubtask(subtaskId, subtask);
        save();
    }
}
