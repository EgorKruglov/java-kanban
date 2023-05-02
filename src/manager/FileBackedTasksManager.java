package manager;                    // Это новый класс!
import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    private void save() { // Сохраняет текущее состояние менеджера в файл
        try (Writer fileWriter = new FileWriter(file)) {

            fileWriter.write("id,type,name,status,description,epic\n"); // Напишем первую строку файла csv

            ArrayList<Task> tasks = super.getTasksList();
            ArrayList<Subtask> subtasks = super.getSubTasksList();
            ArrayList<Epic> epics = super.getEpicsList();
            ArrayList<Task> history = super.getHistory();

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

            StringJoiner joiner = new StringJoiner(","); // Добавляем id задач истории в файл
            for (Task task : history) {
                joiner.add(String.valueOf(task.getId()));
            }
            fileWriter.write(joiner.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Task StringToTask(String value) {
        String[] values = value.split(";");

        if (values[1].equals("TASK")) {
            return new Task(Integer.parseInt(values[0]), values[2], values[4], Status.valueOf(values[3]));
        }
        if (values[1].equals("EPIC")) {
            return new Epic(Integer.parseInt(values[0]), values[2], values[4]);
        }
        if (values[1].equals("SUBTASK")) {
            return new Subtask(Integer.parseInt(values[0]), values[2], values[4], Integer.parseInt(values[5]),
                    Status.valueOf(values[3]));
        }

        return null;
    }

//    public static String historyToString(HistoryManager manager) {}

/*    public static List<Integer> historyFromString(String value) {
        try {
            return Files.readAllLines(Path.of(value)); //
        } catch (IOException e) {
            System.out.println("Файл не найден");
            return Collections.emptyList();
        }
    }*/

    @Override
    public Integer tickIdAndGet() {
        return super.tickIdAndGet();
    }

    @Override
    public Integer getIdCounter() {
        return super.getIdCounter();
    }

    @Override
    public void addTask(Task task) { // Достаточно переопределить каждую модифицирующую операцию
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
    public ArrayList<Task> getTasksList() {
        return super.getTasksList();
    }

    @Override
    public ArrayList<Subtask> getSubTasksList() {
        return super.getSubTasksList();
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return super.getEpicsList();
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
    public void updateTask(Integer taskId, Task task) {
        super.updateTask(taskId, task);
    }

    @Override
    public void updateEpic(Integer epicId, Epic epic) {
        super.updateEpic(epicId, epic);
    }

    @Override
    public void updateSubtask(Integer subtaskId, Subtask subtask) {
        super.updateSubtask(subtaskId, subtask);
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
    public ArrayList<Subtask> getSubtasksByEpic(Integer epicId) {
        return super.getSubtasksByEpic(epicId);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }
}
