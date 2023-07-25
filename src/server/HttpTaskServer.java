package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    TaskManager manager;
    Gson gson;

    public HttpTaskServer() throws IOException {
        manager = Managers.getAutoSave("src\\Memory.csv");
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task/", this::processingTasksMethods);
        server.createContext("/tasks/epic/", this::processingEpicsMethods);
        server.createContext("/tasks/subtask/", this::processingSubtasksMethods);
        server.createContext("/tasks/history/", this::processingHistory);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
    }

    private void processingHistory(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();

        switch (method) {
            case "GET" :
                System.out.println("getHistory"); // Получить историю
                String response = gson.toJson(manager.getHistory());
                writeResponse(h, response, 200);
                break;

            default:
                writeResponse(h, "Некорректный HTTP-метод", 400);
        }


    }

    private void processingTasksMethods(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        switch (method) {

            case "POST" :
                if (h.getRequestURI().getQuery() == null) { // Добавить задачу или обновить
                    System.out.println("addTask"); // Добавить
                    String stringTask = new String(h.getRequestBody().readAllBytes(), UTF_8);

                    Task jsonTask;
                    try {
                        jsonTask = gson.fromJson(stringTask, Task.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(h, "Задача не добавлена. Проверьте корректность введённых параметров.", 400);
                        return;
                    }

                    /* Библиотека gson создаёт объекты, не используя их конструкторы, в которых статусу присваивается значение
                     * по умолчанию. Поэтому присвоим вручную.*/
                    if (jsonTask.getStatus() == null) {
                        jsonTask.setStatus(Status.NEW);
                    }

                    if (!manager.addTask(jsonTask)) {
                        writeResponse(h, "Задача не добавлена. Проверьте уникальность id и " +
                                "незанятость периода времени другими задачами.", 400);
                        return;
                    }

                    writeResponse(h, "Задача добавлена", 200);
                } else {
                    System.out.println("updateTask"); // Обновить
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id задачи передан неверно", 400);
                        return;
                    }

                    String stringTask = new String(h.getRequestBody().readAllBytes(), UTF_8);
                    Task jsonTask;
                    try {
                        jsonTask = gson.fromJson(stringTask, Task.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(h, "Задача не обновлена. Проверьте корректность введённых параметров.", 400);
                        return;
                    }

                    if (id != jsonTask.getId()) {
                        writeResponse(h, "id в url и в теле не совпадают.", 400);
                        return;
                    }

                    if (!manager.updateTask(id, jsonTask)) {
                        writeResponse(h, "Задача не обновлена. Проверьте id и " +
                                "незанятость периода времени другими задачами.", 400);
                        return;
                    }
                    writeResponse(h, "Задача обновлена", 200);
                }
                break;

            case "GET" :
                if (h.getRequestURI().getQuery() == null) { // Получить все задачи или задачу по id
                    System.out.println("getTaskList"); // Получить все
                    List<Task> tasksList = manager.getTasksList();
                    String response = gson.toJson(tasksList);
                    writeResponse(h, response, 200);
                } else {
                    System.out.println("getTaskById"); // Получить задачу по id
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id задачи передан неверно", 400);
                        return;
                    }

                    Task task;
                    try {
                        task = manager.getTask(id);
                    } catch (NullPointerException e) {
                        writeResponse(h, "Задачи с id=" + id + " не найдено", 400);
                        return;
                    }

                    writeResponse(h, gson.toJson(task), 200);
                }
                break;

            case "DELETE" :
                if (h.getRequestURI().getQuery() == null) { // Удалить все задачи или задачу по id
                    System.out.println("deleteAllTasks"); // Удалить все
                    manager.deleteAllTasks();
                    writeResponse(h, "Все задачи удалены", 200);
                } else {
                    System.out.println("deleteTaskById"); // Удалить по id
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id задачи передан неверно", 400);
                        return;
                    }

                    if (!manager.deleteTask(id)) {
                        writeResponse(h, "Задачи с id=" + id + " не найдено", 400);
                        return;
                    }

                    writeResponse(h, "Задача удалена", 200);
                }
                break;

            default:
                writeResponse(h, "Некорректный HTTP-метод", 400);
        }
    }

    private void processingEpicsMethods(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        switch (method) {
            case "POST" :
                if (h.getRequestURI().getQuery() == null) {
                    System.out.println("addEpic");
                    String stringEpic = new String(h.getRequestBody().readAllBytes(), UTF_8);
                    Epic jsonEpic;

                    try {
                        jsonEpic = gson.fromJson(stringEpic, Epic.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(h, "Эпик не добавлен. Проверьте корректность введённых параметров.", 400);
                        return;
                    }

                    if (!manager.addEpic(jsonEpic)) {
                        writeResponse(h, "Эпик не добавлен. Проверьте уникальность id и корректность " +
                                "других параметров.", 400);
                        return;
                    }
                    writeResponse(h, "Эпик добавлен", 200);

                } else {
                    System.out.println("updateEpic"); // Обновить
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id эпика передан неверно", 400);
                        return;
                    }

                    String stringEpic = new String(h.getRequestBody().readAllBytes(), UTF_8);
                    Epic jsonEpic;
                    try {
                        jsonEpic = gson.fromJson(stringEpic, Epic.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(h, "Эпик не обновлён. Проверьте корректность введённых параметров.", 400);
                        return;
                    }

                    if (id != jsonEpic.getId()) {
                        writeResponse(h, "id в url и в теле не совпадают.", 400);
                        return;
                    }

                    if (!manager.updateEpic(id, jsonEpic)) {
                        writeResponse(h, "Эпик не обновлен. Проверьте id эпика и корректность других параметры.", 400);
                        return;
                    }
                    writeResponse(h, "Эпик обновлен", 200);
                }
                break;

            case "GET" :
                if (h.getRequestURI().getQuery() == null) { // Получить все эпики или эпик по id
                    System.out.println("getEpicsList"); // Получить все
                    List<Epic> epicsList = manager.getEpicsList();
                    String response = gson.toJson(epicsList);
                    writeResponse(h, response, 200);
                } else {
                    System.out.println("getEpicById"); // Получить эпик по id
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id эпика передан неверно", 400);
                        return;
                    }

                    Epic epic;
                    try {
                        epic = manager.getEpic(id);
                    } catch (NullPointerException e) {
                        writeResponse(h, "Эпика с id=" + id + " не найдено", 400);
                        return;
                    }

                    writeResponse(h, gson.toJson(epic), 200);
                }
                break;

            case "DELETE" :
                if (h.getRequestURI().getQuery() == null) { // Удалить все эпики или эпик по id
                    System.out.println("deleteAllEpics"); // Удалить все
                    manager.deleteAllEpics();
                    writeResponse(h, "Все эпики удалены", 200);
                } else {
                    System.out.println("deleteEpicById"); // Удалить по id
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id эпика передан неверно", 400);
                        return;
                    }

                    if (!manager.deleteEpic(id)) {
                        writeResponse(h, "Эпика с id=" + id + " не найдено", 400);
                        return;
                    }

                    writeResponse(h, "Эпик удален", 200);
                }
                break;

            default:
                writeResponse(h, "Некорректный HTTP-метод", 400);
        }
    }

    private void processingSubtasksMethods(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();
        switch (method) {
            case "POST" :
                if (h.getRequestURI().getQuery() == null) { // Обновить подзадачу или создать новую
                    System.out.println("addSubtask"); // Создать новую

                    String stringSubTask = new String(h.getRequestBody().readAllBytes(), UTF_8);
                    Subtask jsonSubtask;
                    try {
                        jsonSubtask = gson.fromJson(stringSubTask, Subtask.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(h, "Подзадача не добавлена. Проверьте корректность введённых параметров", 400);
                        return;
                    }

                    /* Библиотека gson создаёт объекты, не используя их конструкторы, в которых статусу присваивается значение
                     * по умолчанию. Поэтому присвоим вручную.*/
                    if (jsonSubtask.getStatus() == null) {
                        jsonSubtask.setStatus(Status.NEW);
                    }

                    if (!manager.addSubtask(jsonSubtask)) {
                        writeResponse(h, "Подзадача не добавлена. Проверьте уникальность id подзадачи, " +
                                "id эпика и незанятость периода времени другими задачами.", 400);
                        return;
                    }
                    writeResponse(h, "Подзадача добавлена", 200);
                } else {
                    System.out.println("updateSubtask"); // Обновить
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id задачи передан неверно", 400);
                        return;
                    }

                    String stringSubTask = new String(h.getRequestBody().readAllBytes(), UTF_8);
                    Subtask jsonSubtask;
                    try {
                        jsonSubtask = gson.fromJson(stringSubTask, Subtask.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(h, "Подзадача не обновлена. Проверьте корректность введённых параметров", 400);
                        return;
                    }

                    if (id != jsonSubtask.getId()) {
                        writeResponse(h, "id в url и в теле не совпадают.", 400);
                        return;
                    }

                    if (!manager.updateTask(id, jsonSubtask)) {
                        writeResponse(h, "Подзадача не обновлена. Проверьте id, id эпика и " +
                                "незанятость периода времени другими задачами.", 400);
                        return;
                    }
                    writeResponse(h, "Подзадача обновлена", 200);
                }
                break;

            case "GET" :
                if (h.getRequestURI().getQuery() == null) { // Получить все задачи или задачу по id
                    System.out.println("getSubtasksList");
                    List<Subtask> subtasksList = manager.getSubTasksList();
                    String response = gson.toJson(subtasksList);
                    writeResponse(h, response, 200);
                } else {
                    System.out.println("getSubtaskById"); // Получить задачу по id
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id подзадачи передан неверно", 400);
                        return;
                    }

                    Subtask subtask;
                    try {
                        subtask = manager.getSubtask(id);
                    } catch (NullPointerException e) {
                        writeResponse(h, "Задачи с id=" + id + " не найдено", 400);
                        return;
                    }
                    writeResponse(h, gson.toJson(subtask), 200);
                }
                break;

            case "DELETE" :
                if (h.getRequestURI().getQuery() == null) { // Удалить все подзадачи или подзадачу по id
                    System.out.println("deleteAllSubtasks"); // Удалить все
                    manager.deleteAllSubtasks();
                    writeResponse(h, "Все подзадачи удалены", 200);
                } else {
                    System.out.println("deleteSubtaskById"); // Удалить по id
                    int id;
                    try {
                        id = Integer.parseInt(h.getRequestURI().getQuery().substring(3));
                    } catch (NumberFormatException e) {
                        writeResponse(h, "id подзадачи передан неверно", 400);
                        return;
                    }

                    if (!manager.deleteSubTask(id)) {
                        writeResponse(h, "Подзадачи с id=" + id + " не найдено", 400);
                        return;
                    }
                    writeResponse(h, "Подзадача удалена", 200);
                }

                System.out.println("deleteAllSubtasks");
                manager.deleteAllSubtasks();
                break;

            default:
                writeResponse(h, "Некорректный HTTP-метод", 400);
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if(responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(UTF_8);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/\n");
        server.start();
    }
}
