import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {  // Класс для управления задачами и эпиками
    private Integer idCounter;  // Счётчик-идентификатор для задач
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public Integer tickIdAndGet() {    // делает тик и возвращает значение
        idCounter++;
        return idCounter;
    }

    public void addTask(Task task) {   // Создание Задачи
        tasks.put(idCounter, task);
    }

    public void addEpic(Epic epic) {   // Создание Эпика
        epics.put(idCounter, epic);
    }

    public void addSubtask(Integer epicId, Subtask subtask) {
        epics.get(epicId).addSubTaskId(idCounter); // Добавление id подзадачи в Эпик
        subtasks.put(idCounter, subtask);
        String newStatus = updateEpicStatus(epics.get(epicId));
        epics.get(epicId).setStatus(newStatus); // Обновление статуса эпика
    }

    public ArrayList<Task> getTasksList() {   // Вернуть список задач(без индексов)
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubTasksList() {   // Вернуть список подзадач
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpicsList() {   // Вернуть список эпиков
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {    // Удалить все задачи
        tasks.clear();
    }

    public void deleteAllEpics() {    // Удалить все эпики
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {    // Удалить все подзадачи
        subtasks.clear();
    }

    public Task getTask(Integer id) {  // Получение задачи
        return tasks.get(id);
    }

    public Epic getEpic(Integer id) {  // Получение эпика
        return epics.get(id);
    }

    public Subtask getSubtask(Integer id) {    // Получение подзадачи
        return subtasks.get(id);
    }

    public void updateTask(Integer taskId, Task task) { // Обновление задачи
        tasks.put(taskId, task);
    }

    public void updateEpic(Integer epicId, Epic epic) { // Обновление эпика
        epics.put(epicId, epic);
    }

    //public void updateSubtask(Integer subtaskId, String title, String description, String status) { // Обновление подзадачи
    public void updateSubtask(Integer subtaskId, Subtask subtask) { // Обновление подзадачи
        subtasks.put(subtaskId, subtask);
        Integer epicId = subtask.getEpicId();
        String newStatus = updateEpicStatus(epics.get(epicId)); // Обновление статуса эпика
        epics.get(epicId).setStatus(newStatus);
    }

    public void deleteTask(Integer taskId) {   // Удалить задачу
        tasks.remove(taskId);
    }

    public void deleteEpic(Integer epicId) { // Удалить эпик
        for (Integer taskId : epics.get(epicId).getSubTasksId()) { // Сначала удалить все подзадачи
            subtasks.remove(taskId);
        }
        epics.remove(epicId);
    }

    public void deleteSubTask(Integer subTaskId) { // Удалить подзадачу
        Integer epicId = subtasks.get(subTaskId).getEpicId(); // Найдём id эпика
        epics.get(epicId).removeSubTaskId(subTaskId);  // Удаляем из эпика
        subtasks.remove(subTaskId);    // Удаляем из подзадач
        String newStatus = updateEpicStatus(epics.get(epicId)); // Обновление статуса эпика
        epics.get(epicId).setStatus(newStatus);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Integer epicId) {   // Получить подзадачи эпика
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Integer taskId : epics.get(epicId).getSubTasksId()) {
            subtasksByEpic.add(subtasks.get(taskId));
        }
        return subtasksByEpic;
    }

    public Integer getIdCounter() {
        return idCounter;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    private String updateEpicStatus (Epic epic) { // Изменяет статус эпика
    /* Если есть хоть одна подзадача "IN_PROGRESS", значит статус эпика "IN_PROGRESS". Если не будет ни одной, значит
    возможно три варианта:
    * Все "DONE"
    * Все "NEW"
    * Есть и "DONE" и "NEW".
    * С помощью флагов и условий можно это эффективно проверить и найти статус эпика. */

        if (epic.getSubTasksId().size() == 0) return "NEW"; // Если передали эпик без подзадач

        boolean isDoneContains = false;
        boolean isNewContains = false;
        for (Integer subtaskID : epic.getSubTasksId()) {
            if (subtasks.get(subtaskID).getStatus().equals("В ПРОЦЕССЕ")) {
                return "IN PROCESS";
            }
            // Если уже найдена, то не проверять. // Ищет "NEW" подзадачу
            if (!(isNewContains) && subtasks.get(subtaskID).getStatus().equals("NEW")) isNewContains = true;
            // Ищет "DONE" подзадачу
            if (!(isDoneContains) && subtasks.get(subtaskID).getStatus().equals("DONE")) isDoneContains = true;
        }
        if (isNewContains) {
            if (isDoneContains) {
                return "IN PROCESS";
            } else {
                return "NEW";
            }
        }
        if (isDoneContains) {
            return "DONE";
        }
        return "???";
    }
}
