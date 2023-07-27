package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import server.util.KVTaskClient;
import server.util.LocalDateTimeAdapter;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String uri) throws IOException, InterruptedException {
        client = new KVTaskClient(uri);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        loadData(); // Восстановление менеджера
    }

    private void loadData() { // Получить состояние менеджера
        /* Получить задачи */
        try {
            String response = client.load("task");
            if (response.isEmpty()) {
                return;
            }

            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i ++) {
                this.addTaskWithoutSave(gson.fromJson(jsonArray.get(i), Task.class));
            }

            System.out.println("Задачи успешно загружены");
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            System.out.println("Не удалось загрузить задачи");
        }

        /* Получить эпики */
        try {
            String response = client.load("epic");
            if (response.isEmpty()) {
                return;
            }

            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                Epic epic = gson.fromJson(jsonElement, Epic.class);
                epic.setSubTasksId(new ArrayList<>());
                this.addEpicWithoutSave(epic);
            }

            System.out.println("Эпики успешно загружены");
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            System.out.println("Не удалось загрузить эпики");
        }

        /* Получить подзадачи */
        try {
            String response = client.load("subtask");
            if (response.isEmpty()) {
                return;
            }

            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                this.addSubtaskWithoutSave(gson.fromJson(jsonArray.get(i), Subtask.class));
            }

            System.out.println("Подзадачи успешно загружены");
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            System.out.println("Не удалось загрузить подзадачи");
        }

        /* Получить историю */
        try {
            String response = client.load("history");
            if (response.isEmpty()) {
                return;
            }

            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                /* Определяем тип по характерным полям */
                if (jsonObject.get("subTasksId") != null) {
                    this.addInHistory(gson.fromJson(jsonElement, Epic.class));
                } else if (jsonObject.get("epicId") != null) {
                    this.addInHistory(gson.fromJson(jsonElement, Subtask.class));
                } else {
                    this.addInHistory(gson.fromJson(jsonElement, Task.class));
                }
            }

            System.out.println("История успешно загружена");
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            System.out.println("Не удалось загрузить историю");
        }
    }

    @Override
    public void save() { // Сохранить состояние менеджера
        try {
            client.put("task", gson.toJson(this.getTasksList()));
        } catch (IOException | InterruptedException e) {
            System.out.println("Не удалось сохранить задачи");
        }
        try {
            client.put("epic", gson.toJson(this.getEpicsList()));
        } catch (IOException | InterruptedException e) {
            System.out.println("Не удалось сохранить эпики");
        }
        try {
            client.put("subtask", gson.toJson(this.getSubTasksList()));
        } catch (IOException | InterruptedException e) {
             System.out.println("Не удалось сохранить подзадачи");
        }
        try {
            client.put("history", gson.toJson(this.getHistory()));
        } catch (IOException | InterruptedException e) {
            System.out.println("Не удалось сохранить историю");
        }
    }
}
