import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {  // Класс для управления задачами и эпиками
    Integer idCounter;  // Счётчик-идентификатор для задач
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(String title, String description) {   // Создание Задачи
        idCounter += 1;
        tasks.put(idCounter, new Task(title, description));
    }

    public void addEpic(String title, String description) {   // Создание Эпика
        idCounter += 1;
        epics.put(idCounter, new Epic(title, description));
    }

    public void addSubtask(Integer epicId, String title, String description) {    // Создание подзадачи
        idCounter += 1;
        epics.get(epicId).subTasksId.add(idCounter);    // Добавление id подзадачи в Эпик
        epics.get(epicId).status = "NEW";
        subtasks.put(idCounter, new Subtask(title, description));
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

    public void updateTask(Integer taskId, String title, String description, String status) { // Обновление задачи
        tasks.put(taskId, new Task(title, description, status));
    }

    public void updateEpic(Integer epicId, String title, String description) { // Обновление эпика
        epics.put(epicId, new Epic(title, description));
    }

    public void updateSubtask(Integer subtaskId, String title, String description, String status) { // Обновление подзадачи
        subtasks.put(subtaskId, new Subtask(title, description, status));
        // Если подзадача выполнена, надо проверить другие подзадачи и изменить статус эпика.
        // Если подзадача "в процессе", надо изменить статус эпику.
        if (!status.equals("NEW")) {
            Integer targetEpicId = 0;   // Найдём id нужного эпика /*В буд. сабтаски могут знать, к кому отсносятся*/
            for (Integer epicId: epics.keySet()) {
                if (epics.get(epicId).subTasksId.contains(subtaskId)) {
                    targetEpicId = epicId;
                    break;
                }
            }

            boolean isEpicInProcess = false; // Если остался false, эпик не в процессе
            boolean isEpicDone = true; // Если остался true, то эпик выполнен
            for (Integer taskId : epics.get(targetEpicId).subTasksId) {
                if (!subtasks.get(taskId).status.equals("DONE")) {  // Если одна задача не выполнена, эпик не выполнен
                    isEpicDone = false;
                }
                if (!subtasks.get(taskId).status.equals("NEW")) { // Если хотя бы одна не NEW, то епик в "процессе"
                    isEpicInProcess = true; /*С помощью ещё одно флага можно исключить лишние проверки(на будущее)*/
                }
            }

            if (isEpicDone) {   // Меняет статус
                epics.get(targetEpicId).status = "DONE";
            } else if (isEpicInProcess) {
                epics.get(targetEpicId).status = "IN_PROCESS";
            }
        }
    }

    public void deleteTask(Integer taskId) {   // Удалить задачу
        tasks.remove(taskId);
    }

    public void deleteEpic(Integer epicId) {   // Удалить эпик
        for (Integer taskId : epics.get(epicId).subTasksId) {   // Сначала удалить все подзадачи
            subtasks.remove(taskId);
        }
        epics.remove(epicId);
    }

    public void deleteSubTask(Integer taskId) {    // Удалить подзадачу
        Integer targetEpicId = 0;   // Найдём id нужного эпика
        for (Integer epicId: epics.keySet()) {
            if (epics.get(epicId).subTasksId.contains(taskId)) {
                targetEpicId = epicId;
                break;
            }
        }

        epics.get(targetEpicId).subTasksId.remove(taskId);  // Удаляем из эпика
        subtasks.remove(taskId);    // Удаляем из подзадач

        // Если есть другие подздалачи, надо проверить статус эпика
        if (epics.get(targetEpicId).subTasksId.size() > 0) {
            boolean isEpicInProcess = false; // Если остался false, эпик не в процессе
            boolean isEpicDone = true; // Если остался true, то эпик выполнен
            for (Integer SubtaskId : epics.get(targetEpicId).subTasksId) {
                if (!subtasks.get(SubtaskId).status.equals("DONE")) {  // Если одна задача не выполнена, эпик не выполнен
                    isEpicDone = false;
                }
                if (!subtasks.get(SubtaskId).status.equals("NEW")) { // Если хотя бы одна не NEW, то епик в "процессе"
                    isEpicInProcess = true; /*С помощью ещё одно флага можно исключить лишние проверки(на будущее)*/
                }
            }

            if (isEpicDone) {   // Меняет статус эпика
                epics.get(targetEpicId).status = "DONE";
            } else if (isEpicInProcess) {
                epics.get(targetEpicId).status = "IN_PROCESS";
            }
        }
    }

    public ArrayList<Subtask> getSubtasksByEpic(Integer epicId) {   // Получить подзадачи эпика
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        for (Integer taskId : epics.get(epicId).subTasksId) {
            subtasksByEpic.add(subtasks.get(taskId));
        }
        return subtasksByEpic;
    }

}
