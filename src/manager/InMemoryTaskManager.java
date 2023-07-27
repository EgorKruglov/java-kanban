package manager;

import extraExceptions.TaskPeriodConflictException;
import manager.util.TaskComparator;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {  // Этот класс хранит задачи в оперативной памяти
    private Integer idCounter;  // Счётчик-идентификатор для задач
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(new TaskComparator());
    }

    @Override
    public Integer tickIdAndGet() {    // делает тик и возвращает значение
        idCounter++;
        return idCounter;
    }

    @Override
    public Boolean addTask(Task task) {   // Создание Задачи
        if (tasks.containsKey(task.getId())) {
            return false;
        }
        if (isTaskPeriodConflict(task)) {
            return false;
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return true;
    }

    @Override
    public Boolean addEpic(Epic epic) {   // Создание Эпика
        if (epics.containsKey(epic.getId())) {
            return false;
        }
        epics.put(epic.getId(), epic);
        return true;
    }

    @Override
    public Boolean addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return false;
        }
        if (isTaskPeriodConflict(subtask)) {
            return false;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return false;
        }
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTaskId(subtask.getId()); // Добавление id подзадачи в Эпик
        epic.updateEpic(subtasks); // Обновление статуса эпика
        prioritizedTasks.add(subtask);
        return true;
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
        for (Task task : tasks.values()) {  // Удалить из истории и сортированного списка
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {  // Удалить все эпики
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {    // Удалить все подзадачи
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.updateEpic(subtasks);
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
    public Boolean updateTask(Integer taskId, Task task) { // Обновление задачи
        if (!tasks.containsKey(taskId)) {
            return false;
        }
        if (isTaskPeriodConflict(task)) {
            return false;
        }
        tasks.put(taskId, task);
        updatePrioritizedTasks(task);
        return true;
    }

    @Override
    public Boolean updateEpic(Integer epicId, Epic epic) { // Обновление эпика
        if (!epics.containsKey(epicId)) {
            return false;
        }
        List<Integer> subTasksId = epics.get(epicId).getSubTasksId();
        epic.setSubTasksId(subTasksId);
        epics.put(epicId, epic);
        return true;
    }

    @Override
    public Boolean updateSubtask(Integer subtaskId, Subtask subtask) { // Обновление подзадачи
        if (!subtasks.containsKey(subtaskId)) {
            return false;
        }
        if (isTaskPeriodConflict(subtask)) {
            return false;
        }
        subtasks.put(subtaskId, subtask);
        epics.get(subtask.getEpicId()).updateEpic(subtasks);
        updatePrioritizedTasks(subtask);
        return true;
    }

    @Override
    public Boolean deleteTask(Integer taskId) { // Удалить задачу
        if (!tasks.containsKey(taskId)) {
            return false;
        }
        prioritizedTasks.remove(tasks.get(taskId));
        historyManager.remove(taskId); // Удалить из истории
        tasks.remove(taskId);
        return true;
    }

    @Override
    public Boolean deleteEpic(Integer epicId) { // Удалить эпик
        if (!epics.containsKey(epicId)) {
            return false;
        }
        for (Integer taskId : epics.get(epicId).getSubTasksId()) { // Сначала удалить все подзадачи
            prioritizedTasks.remove(subtasks.get(taskId));
            subtasks.remove(taskId);
            historyManager.remove(taskId); // Удалить из истории
        }
        epics.remove(epicId);
        historyManager.remove(epicId); // Удалить из истории
        return true;
    }

    @Override
    public Boolean deleteSubTask(Integer subtaskId) { // Удалить подзадачу
        if (!subtasks.containsKey(subtaskId)) {
            return false;
        }
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        prioritizedTasks.remove(subtasks.get(subtaskId));
        subtasks.remove(subtaskId); // Удаляем из подзадач
        historyManager.remove(subtaskId); // Удалить из истории
        epic.removeSubTaskId(subtaskId);  // Удаляем из эпика
        epic.updateEpic(subtasks); // Обновление статуса эпика
        return true;
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Integer epicId) {   // Получить подзадачи эпика
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Integer taskId : epics.get(epicId).getSubTasksId()) {
            subtasksByEpic.add(subtasks.get(taskId));
        }
        return subtasksByEpic;
    }

    private void updatePrioritizedTasks(Task newTask) {
        Iterator<Task> iterator = prioritizedTasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId().equals(newTask.getId())) {
                iterator.remove();
                break;
            }
        }
        prioritizedTasks.add(newTask);
    }

    private boolean isTaskPeriodConflict(Task newTask) { // Проверка на наложение сроков задач друг на друга
        if (newTask.getStartTime() == null) { // Без заданного начала отправляются в конец списка
            return false;
        }

        LocalDateTime newTaskEndTime = newTask.getEndTime();
        for (Task task : prioritizedTasks) {
            if (Objects.equals(task.getId(), newTask.getId())) { // Чтобы пропустила саму себя
                continue;
            }
            if (task.getStartTime() != null) {
                LocalDateTime taskEndTime = task.getEndTime();
                // Если оба условия снизу true, то нет конфликта периодов
                boolean firstBool = newTaskEndTime.isBefore(task.getStartTime());
                boolean secondBool = newTask.getStartTime().isAfter(taskEndTime);

                if (!firstBool && !secondBool) {
                    try {
                        throw new TaskPeriodConflictException("Ошибка: Задача накладывается на другие задачи");
                    } catch (TaskPeriodConflictException e) {
                        System.out.println(e.getMessage());
                    }
                    return true;
                }
            }
        }
        return false;
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
