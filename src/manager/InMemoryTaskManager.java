package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {  // Этот класс хранит задачи в оперативной памяти
    private Integer idCounter;  // Счётчик-идентификатор для задач
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(Integer idCounter,
                               Map<Integer, Task> tasks,
                               Map<Integer, Epic> epics,
                               Map<Integer, Subtask> subtasks,
                               HistoryManager historyManager) {  // Конструктор для восстановления менеджера
        this.idCounter = idCounter;
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
        this.historyManager = historyManager;
    }

    @Override
    public Integer tickIdAndGet() {    // делает тик и возвращает значение
        idCounter++;
        return idCounter;
    }

    @Override
    public void addTask(Task task) {   // Создание Задачи
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {   // Создание Эпика
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Integer epicId = subtask.getEpicId();
        epics.get(epicId).addSubTaskId(subtask.getId()); // Добавление id подзадачи в Эпик
        subtasks.put(subtask.getId(), subtask);
        Status newStatus = updateEpicStatus(epics.get(epicId));
        epics.get(epicId).setStatus(newStatus); // Обновление статуса эпика
    }

    public void addInHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public List<Task> getTasksList() {   // Вернуть список задач(без индексов)
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubTasksList() {   // Вернуть список подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {   // Вернуть список эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {  // Удалить все задачи
        for (Integer id : tasks.keySet()) {  // Удалить из истории
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {  // Удалить все эпики
        for (Epic epic : epics.values()) {  // Удалить из истории подзадачи эпика
            for (Integer subTaskId : epic.getSubTasksId()) {
                historyManager.remove(subTaskId);
            }
            historyManager.remove(epic.getId());  // Удалить из истории эпик
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {    // Удалить все подзадачи
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Task getTask(Integer id) {  // Получение задачи
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(Integer id) {  // Получение эпика
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(Integer id) {    // Получение подзадачи
        historyManager.add(subtasks.get(id));
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
        Status newStatus = updateEpicStatus(epics.get(epicId)); // Обновление статуса эпика
        epics.get(epicId).setStatus(newStatus);
    }

    @Override
    public void deleteTask(Integer taskId) { // Удалить задачу
        tasks.remove(taskId);
        historyManager.remove(taskId); // Удалить из истории
    }

    @Override
    public void deleteEpic(Integer epicId) { // Удалить эпик
        for (Integer taskId : epics.get(epicId).getSubTasksId()) { // Сначала удалить все подзадачи
            subtasks.remove(taskId);
            historyManager.remove(taskId); // Удалить из истории
        }
        epics.remove(epicId);
        historyManager.remove(epicId); // Удалить из истории
    }

    @Override
    public void deleteSubTask(Integer subTaskId) { // Удалить подзадачу
        Integer epicId = subtasks.get(subTaskId).getEpicId(); // Найдём id эпика
        epics.get(epicId).removeSubTaskId(subTaskId);  // Удаляем из эпика
        subtasks.remove(subTaskId); // Удаляем из подзадач
        historyManager.remove(subTaskId); // Удалить из истории
        Status newStatus = updateEpicStatus(epics.get(epicId)); // Обновление статуса эпика
        epics.get(epicId).setStatus(newStatus);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Integer epicId) {   // Получить подзадачи эпика
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
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    private Status updateEpicStatus (Epic epic) { // Изменяет статус эпика
    /* Если есть хоть одна подзадача "IN_PROGRESS", значит статус эпика "IN_PROGRESS". Если не будет ни одной, значит
    возможно три варианта:
    * Все "DONE"
    * Все "NEW"
    * Есть и "DONE" и "NEW".
    * С помощью флагов и условий можно это эффективно проверить и найти статус эпика. */

        if (epic.getSubTasksId().size() == 0) return Status.NEW; // Если передали эпик без подзадач

        boolean isDoneContains = false;
        boolean isNewContains = false;
        for (Integer subtaskID : epic.getSubTasksId()) {
            if (subtasks.get(subtaskID).getStatus().equals(Status.IN_PROGRESS)) {
                return Status.IN_PROGRESS;
            }
            // Если уже найдена, то не проверять. // Ищет "NEW" подзадачу
            if (!(isNewContains) && subtasks.get(subtaskID).getStatus().equals(Status.NEW)) isNewContains = true;
            // Ищет "DONE" подзадачу
            if (!(isDoneContains) && subtasks.get(subtaskID).getStatus().equals(Status.DONE)) isDoneContains = true;
        }
        if (isNewContains) {
            if (isDoneContains) {
                return Status.IN_PROGRESS;
            } else {
                return Status.NEW;
            }
        }
        if (isDoneContains) {
            return Status.DONE;
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
