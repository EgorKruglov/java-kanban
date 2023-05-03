package manager;                    // Это новый класс!
import task.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager(File file,  // Конструктор для восстановления менеджера
                                  Integer idCounter,
                                  Map<Integer, Task> tasks,
                                  Map<Integer, Epic> epics,
                                  Map<Integer, Subtask> subtasks,
                                  HistoryManager historyManager) {
        super(idCounter, tasks, epics, subtasks, historyManager);
        this.file = file;

    }

    public static void main(String[] args) {  // Метод для проверки сериализации
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

        FileBackedTasksManager backTester = loadFromFile(new File("C:\\Users\\admin\\dev\\java-kanban\\src\\Memory.csv"));
    }

    private void save() { // Сохраняет текущее состояние менеджера в файл
        try (Writer fileWriter = new FileWriter(file)) {

            if (file == null) {
                throw new ManagerSaveException("Не удалось найти файл");
            }

            fileWriter.write("\nid,type,name,status,description,epic\n"); // Напишем первую строку файла csv

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

        } catch (ManagerSaveException e) {  // Я не до конца понял, где нужно его выбрасывать...
            System.out.println(e.getMessage());
        }
        catch (FileNotFoundException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            e.printStackTrace();
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

    /*Правильно ли я понимаю, что мне нужно восстановить все поля класса InMemoryTaskManager при сериализации?*/
    public static FileBackedTasksManager loadFromFile(File file) {

        int idCounter = 0;  // Счётчик-идентификатор для задач
        final Map<Integer, Task> tasks = new HashMap<>();
        final Map<Integer, Epic> epics = new HashMap<>();
        final Map<Integer, Subtask> subtasks = new HashMap<>();
        final HistoryManager historyManager = Managers.getDefaultHistory();

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 2; i < lines.size() - 2; i++) { // В первых и последних двух сроках нет задач

                String[] lineValues = lines.get(i).split(";"); // Парсим по строке из файла
                Integer taskId = Integer.parseInt(lineValues[0]);

                if (idCounter < taskId) {  // Находим актуальный, наибольший id в файле
                    idCounter = taskId;
                }

                switch (TaskName.valueOf(lineValues[1])) {  // Добавляем таски в их хештаблицы
                    case TASK:
                        tasks.put(taskId,
                                new Task(taskId,
                                        lineValues[2],
                                        lineValues[4],
                                        Status.valueOf(lineValues[3])));
                        break;
                    case EPIC:
                        epics.put(taskId,
                                new Epic(taskId,
                                        lineValues[2],
                                        lineValues[4]));
                        break;
                    case SUBTASK:
                        subtasks.put(taskId,
                                new Subtask(taskId,
                                        lineValues[2],
                                        lineValues[4],
                                        Integer.parseInt(lineValues[5]),
                                        Status.valueOf(lineValues[3])));
                        break;
                    default:
                        System.out.println("Задача не распознана");
                }
            }

            // Создаём historyManager
            Integer[] historyValues = historyFromString(lines.get(lines.size()-1));

            for (Integer history : historyValues) {
                if (tasks.containsKey(history)) {
                    historyManager.add(tasks.get(history));
                } else if (subtasks.containsKey(history)) {
                    historyManager.add(subtasks.get(history));
                } else if (epics.containsKey(history)) {
                    historyManager.add(epics.get(history));
                }
            }

        } catch (IOException ex) {
            System.out.println("Файл не найден.");
        }
        return new FileBackedTasksManager(file, idCounter, tasks, epics, subtasks, historyManager);
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
    public void addSubtask(Integer epicId, Subtask subtask) {
        super.addSubtask(epicId, subtask);
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

    private static class ManagerSaveException extends IOException {
        public ManagerSaveException(String message) {
            super(message);
        }
    }
}
