package manager;                    // Класс для сериализации

import extraExceptions.ManagerSaveException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import task.TaskName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(String path) {
        this.file = new File(path);
    }

    /* Конструктор, чтобы присвоить путь по умолчанию, когда не передан путь к файлу. */
    public FileBackedTasksManager() {
        file = new File("src\\Memory.csv");
    }

    public void save() { // Сохраняет текущее состояние менеджера в файл
        try (Writer fileWriter = new FileWriter(file)) {

            fileWriter.write("\nid;type;name;status;description;epicId;startTime;duration;endTime\n"); // Напишем первую строку файла csv

            List<Task> tasks = super.getTasksList();
            List<Subtask> subtasks = super.getSubTasksList();
            List<Epic> epics = super.getEpicsList();

            for (Task task : tasks) { // Добавляем созданные задачи в файл
                fileWriter.write(String.format("%s;%s;%s;%s;%s;%s;%s;\n",
                        task.getId(),
                        TaskName.TASK,
                        task.getTitle(),
                        task.getStatus(),
                        task.getDescription(),
                        task.getStartTime(),
                        task.getDuration()));
            }

            for (Epic epic : epics) {
                fileWriter.write(String.format("%s;%s;%s;%s;%s;\n",
                        epic.getId(),
                        TaskName.EPIC,
                        epic.getTitle(),
                        epic.getStatus(),
                        epic.getDescription()));
            }

            for (Subtask subtask : subtasks) {
                fileWriter.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s;\n",
                        subtask.getId(),
                        TaskName.SUBTASK,
                        subtask.getTitle(),
                        subtask.getStatus(),
                        subtask.getDescription(),
                        subtask.getEpicId(),
                        subtask.getStartTime(),
                        subtask.getDuration()));
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
                        if (!lineValues[6].equals("null")) {
                            backTester.addTask(new Task(
                                    taskId,
                                    lineValues[2],
                                    lineValues[4],
                                    Status.valueOf(lineValues[3]),
                                    Integer.parseInt(lineValues[6]),
                                    LocalDateTime.parse(lineValues[5])));
                        } else {
                            backTester.addTask(new Task(
                                    taskId,
                                    lineValues[2],
                                    lineValues[4],
                                    Status.valueOf(lineValues[3])));
                        }
                        break;
                    case EPIC:
                        backTester.addEpic(new Epic(
                                taskId,
                                lineValues[2],
                                lineValues[4]));
                        break;
                    case SUBTASK:
                        if (!lineValues[6].equals("null")) { // Если есть duration и startTime
                            backTester.addSubtask(new Subtask(
                                    taskId,
                                    lineValues[2],
                                    lineValues[4],
                                    Integer.parseInt(lineValues[5]),
                                    Status.valueOf(lineValues[3]),
                                    Integer.parseInt(lineValues[7]),
                                    LocalDateTime.parse(lineValues[6])));
                        } else {
                            backTester.addSubtask(new Subtask(
                                    taskId,
                                    lineValues[2],
                                    lineValues[4],
                                    Integer.parseInt(lineValues[5]),
                                    Status.valueOf(lineValues[3])));
                        }


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
    public Boolean addTask(Task task) {
        Boolean result = super.addTask(task);
        if (result) {
            save();
        }
        return result;
    }

    public Boolean addTaskWithoutSave(Task task) { // Чтобы создавать при восстановлении данных
        return super.addTask(task);
    }

    @Override
    public Boolean addEpic(Epic epic) {
        Boolean result = super.addEpic(epic);
        if (result) {
            save();
        }
        return result;
    }

    public Boolean addEpicWithoutSave(Epic epic) { // Чтобы создавать при восстановлении данных
        return super.addEpic(epic);
    }

    @Override
    public Boolean addSubtask(Subtask subtask) {
        Boolean result = super.addSubtask(subtask);
        if (result) {
            save();
        }
        return result;
    }

    public Boolean addSubtaskWithoutSave(Subtask subtask) { // Чтобы создавать при восстановлении данных
        return super.addSubtask(subtask);
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
    public Boolean deleteTask(Integer taskId) {
        Boolean result = super.deleteTask(taskId);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public Boolean deleteEpic(Integer epicId) {
        Boolean result = super.deleteEpic(epicId);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public Boolean deleteSubTask(Integer subTaskId) {
        Boolean result = super.deleteSubTask(subTaskId);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public Boolean updateTask(Integer taskId, Task task) { // Обновление задачи
        Boolean result = super.updateTask(taskId, task);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public Boolean updateEpic(Integer epicId, Epic epic) { // Обновление эпика
        Boolean result = super.updateEpic(epicId, epic);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public Boolean updateSubtask(Integer subtaskId, Subtask subtask) { // Обновление подзадачи
        Boolean result = super.updateSubtask(subtaskId, subtask);
        if (result) {
            save();
        }
        return result;
    }
}
