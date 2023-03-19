import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {  // Класс для управления задачами и эпиками
    private Integer idCounter;  // Счётчик-идентификатор для задач
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    private final ArrayList<Task> history;

    public InMemoryTaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = new ArrayList<>();
    }

    @Override
    public Integer tickIdAndGet() {    // делает тик и возвращает значение
        idCounter++;
        return idCounter;
    }

    @Override
    public void addTask(Task task) {   // Создание Задачи
        tasks.put(idCounter, task);
    }

    @Override
    public void addEpic(Epic epic) {   // Создание Эпика
        epics.put(idCounter, epic);
    }

    @Override
    public void addSubtask(Integer epicId, Subtask subtask) {
        epics.get(epicId).addSubTaskId(idCounter); // Добавление id подзадачи в Эпик
        subtasks.put(idCounter, subtask);
        Statuses newStatus = updateEpicStatus(epics.get(epicId));
        epics.get(epicId).setStatus(newStatus); // Обновление статуса эпика
    }
    @Override
    public ArrayList<Task> getTasksList() {   // Вернуть список задач(без индексов)
        return new ArrayList<>(tasks.values());
    }
    @Override
    public ArrayList<Subtask> getSubTasksList() {   // Вернуть список подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {   // Вернуть список эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {    // Удалить все задачи
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {    // Удалить все эпики
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {    // Удалить все подзадачи
        subtasks.clear();
    }

    @Override
    public Task getTask(Integer id) {  // Получение задачи
        addInHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(Integer id) {  // Получение эпика
        addInHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(Integer id) {    // Получение подзадачи
        addInHistory(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Integer taskId, Task task) { // Обновление задачи
        tasks.put(taskId, task);
    }

    @Override
    public void updateEpic(Integer epicId, Epic epic) { // Обновление эпика
        epics.put(epicId, epic);
    }

    @Override
    public void updateSubtask(Integer subtaskId, Subtask subtask) { // Обновление подзадачи
        subtasks.put(subtaskId, subtask);
        Integer epicId = subtask.getEpicId();
        Statuses newStatus = updateEpicStatus(epics.get(epicId)); // Обновление статуса эпика
        epics.get(epicId).setStatus(newStatus);
    }

    @Override
    public void deleteTask(Integer taskId) {   // Удалить задачу
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpic(Integer epicId) { // Удалить эпик
        for (Integer taskId : epics.get(epicId).getSubTasksId()) { // Сначала удалить все подзадачи
            subtasks.remove(taskId);
        }
        epics.remove(epicId);
    }

    @Override
    public void deleteSubTask(Integer subTaskId) { // Удалить подзадачу
        Integer epicId = subtasks.get(subTaskId).getEpicId(); // Найдём id эпика
        epics.get(epicId).removeSubTaskId(subTaskId);  // Удаляем из эпика
        subtasks.remove(subTaskId);    // Удаляем из подзадач
        Statuses newStatus = updateEpicStatus(epics.get(epicId)); // Обновление статуса эпика
        epics.get(epicId).setStatus(newStatus);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Integer epicId) {   // Получить подзадачи эпика
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Integer taskId : epics.get(epicId).getSubTasksId()) {
            subtasksByEpic.add(subtasks.get(taskId));
        }
        return subtasksByEpic;
    }

    @Override
    public Integer getIdCounter() {
        return idCounter;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    private Statuses updateEpicStatus (Epic epic) { // Изменяет статус эпика
    /* Если есть хоть одна подзадача "IN_PROGRESS", значит статус эпика "IN_PROGRESS". Если не будет ни одной, значит
    возможно три варианта:
    * Все "DONE"
    * Все "NEW"
    * Есть и "DONE" и "NEW".
    * С помощью флагов и условий можно это эффективно проверить и найти статус эпика. */

        if (epic.getSubTasksId().size() == 0) return Statuses.NEW; // Если передали эпик без подзадач

        boolean isDoneContains = false;
        boolean isNewContains = false;
        for (Integer subtaskID : epic.getSubTasksId()) {
            if (subtasks.get(subtaskID).getStatus().equals(Statuses.IN_PROGRESS)) {
                return Statuses.IN_PROGRESS;
            }
            // Если уже найдена, то не проверять. // Ищет "NEW" подзадачу
            if (!(isNewContains) && subtasks.get(subtaskID).getStatus().equals(Statuses.NEW)) isNewContains = true;
            // Ищет "DONE" подзадачу
            if (!(isDoneContains) && subtasks.get(subtaskID).getStatus().equals(Statuses.DONE)) isDoneContains = true;
        }
        if (isNewContains) {
            if (isDoneContains) {
                return Statuses.IN_PROGRESS;
            } else {
                return Statuses.NEW;
            }
        }
        if (isDoneContains) {
            return Statuses.DONE;
        }
        return null;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    private void addInHistory(Task task) {
        if (task != null) {
            if (history.size() > 9) history.remove(0);
            history.add(task);
        }
    }
}
