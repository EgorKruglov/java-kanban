package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tests.util.TestClient;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    TestClient testClient = new TestClient("http://localhost:8080");
    KVServer kvServer;
    HttpTaskServer taskServer;
    String testString;
    String testString1;
    HttpResponse<String> result;

    @BeforeEach
    void freshProgramBeforeTest() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (java.io.IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void closeDataServer() {
        taskServer.stop();
        kvServer.stop();
    }

    /* Tasks */

    @Test
    void addTask() { // Сохранить задачу
        testString = "incorrect task to ger error";
        result = testClient.sendPost("/tasks/task/", testString);
        assertEquals(400, result.statusCode());

        result = testClient.sendGet("/tasks/task/");
        assertEquals("[]", result.body());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/", testString);
        assertEquals(200, result.statusCode());
        assertEquals("Задача добавлена", result.body());

        result = testClient.sendGet("/tasks/task/?id=4");
        assertEquals(testString, result.body());

        result = testClient.sendPost("/tasks/task/", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Задача не добавлена. Проверьте уникальность id и " +
                "незанятость периода времени другими задачами.", result.body());
    }

    @Test
    void updateTask() {
        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/", testString);

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"IN_PROGRESS\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/?id=4", testString);
        assertEquals(200, result.statusCode());
        assertEquals("Задача обновлена", result.body());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":5,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/?id=4", testString);
        assertEquals(400, result.statusCode());
        assertEquals("id в url и в теле не совпадают.", result.body());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/?id=2", testString);
        assertEquals(400, result.statusCode());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":5,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/?id=5", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Задача не обновлена. Проверьте id и " +
                "незанятость периода времени другими задачами.", result.body());
    }

    @Test
    void getTasks() {
        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        testClient.sendPost("/tasks/task/", testString);

        result = testClient.sendGet("/tasks/task/");
        assertEquals(200, result.statusCode());
        assertEquals("["+testString+"]", result.body());

        testString1 = "{\"title\":\"test1\",\"description\":\"test\",\"status\":\"NEW\",\"id\":5,\"startTime\":\"2011-6-10-12-20\",\"duration\":20}";
        testClient.sendPost("/tasks/task/", testString1);

        result = testClient.sendGet("/tasks/task/");
        assertEquals(200, result.statusCode());
        assertEquals("["+testString+","+testString1+"]", result.body());
    }

    @Test
    void getOneTask() {
        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        testClient.sendPost("/tasks/task/", testString);

        result = testClient.sendGet("/tasks/task/?id=4ggfg");
        assertEquals(400, result.statusCode());

        result = testClient.sendGet("/tasks/task/?id=1");
        assertEquals(400, result.statusCode());

        result = testClient.sendGet("/tasks/task/?id=4");
        assertEquals(200, result.statusCode());
        assertEquals(testString, result.body());
    }

    @Test
    void deleteTasks() {
        result = testClient.sendDelete("/tasks/task/");
        assertEquals(200, result.statusCode());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        testClient.sendPost("/tasks/task/", testString);

        result = testClient.sendDelete("/tasks/task/");
        assertEquals(200, result.statusCode());

        result = testClient.sendGet("/tasks/task/");
        assertEquals("[]", result.body());
    }

    @Test
    void deleteOneTask() {
        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        testClient.sendPost("/tasks/task/", testString);

        result = testClient.sendDelete("/tasks/task/?id=4ggfg");
        assertEquals(400, result.statusCode());

        result = testClient.sendDelete("/tasks/task/?id=1");
        assertEquals(400, result.statusCode());

        result = testClient.sendDelete("/tasks/task/?id=4");
        assertEquals(200, result.statusCode());
        assertEquals("Задача удалена", result.body());

        result = testClient.sendGet("/tasks/task/?id=4");
        assertEquals(400, result.statusCode());
        assertEquals("Задачи с id=4 не найдено", result.body());
    }

    /* Epics */

    @Test
    void addEpic() {
        testString = "incorrect epic to ger error";
        result = testClient.sendPost("/tasks/epic/", testString);
        assertEquals(400, result.statusCode());

        result = testClient.sendGet("/tasks/epic/");
        assertEquals("[]", result.body());

        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        String resultString = "{\"endTime\":null,\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\"," +
                "\"status\":null,\"id\":2,\"startTime\":null,\"duration\":null}";
        result = testClient.sendPost("/tasks/epic/", testString);
        assertEquals(200, result.statusCode());
        assertEquals("Эпик добавлен", result.body());

        result = testClient.sendGet("/tasks/epic/?id=2");
        assertEquals(resultString, result.body());

        result = testClient.sendPost("/tasks/epic/", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Эпик не добавлен. Проверьте уникальность id и корректность " +
                "других параметров.", result.body());
    }

    @Test
    void updateEpic() {
        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        result = testClient.sendPost("/tasks/epic/", testString);

        testString = "{\"subTasksId\":[],\"title\":\"test231\",\"description\":\"testtest\",\"id\":2}";
        result = testClient.sendPost("/tasks/epic/?id=2", testString);
        assertEquals(200, result.statusCode());
        assertEquals("Эпик обновлен", result.body());

        testString = "{\"subTasksId\":[],\"title\":\"test231\",\"description\":\"testtest\",\"id\":hg}";
        result = testClient.sendPost("/tasks/epic/?id=2", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Эпик не обновлён. Проверьте корректность введённых параметров.", result.body());

        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        result = testClient.sendPost("/tasks/epic/?id=3", testString);
        assertEquals(400, result.statusCode());
        assertEquals("id в url и в теле не совпадают.", result.body());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":5,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/?id=5", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Задача не обновлена. Проверьте id и " +
                "незанятость периода времени другими задачами.", result.body());
    }

    @Test
    void getEpics() {
        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        String expectedEpic1 = "{\"endTime\":null,\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\"," +
                "\"status\":null,\"id\":2,\"startTime\":null,\"duration\":null}";
        testString1 = "{\"subTasksId\":[],\"title\":\"test231\",\"description\":\"testtest\",\"id\":3}";
        String expectedEpic2 = "{\"endTime\":null,\"subTasksId\":[],\"title\":\"test231\",\"description\":\"testtest\"," +
                "\"status\":null,\"id\":3,\"startTime\":null,\"duration\":null}";

        testClient.sendPost("/tasks/epic/", testString);

        result = testClient.sendGet("/tasks/epic/");
        assertEquals(200, result.statusCode());
        assertEquals("["+expectedEpic1+"]", result.body());

        testClient.sendPost("/tasks/epic/", testString1);

        result = testClient.sendGet("/tasks/epic/");
        assertEquals(200, result.statusCode());
        assertEquals("["+expectedEpic1+","+expectedEpic2+"]", result.body());
    }

    @Test
    void getOneEpic() {
        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        String expectedEpic1 = "{\"endTime\":null,\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\"," +
                "\"status\":null,\"id\":2,\"startTime\":null,\"duration\":null}";

        testClient.sendPost("/tasks/epic/", testString);

        result = testClient.sendGet("/tasks/epic/?id=4ggfg");
        assertEquals(400, result.statusCode());
        assertEquals("id эпика передан неверно", result.body());

        result = testClient.sendGet("/tasks/epic/?id=1");
        assertEquals(400, result.statusCode());
        assertEquals("Эпика с id=1 не найдено", result.body());

        result = testClient.sendGet("/tasks/epic/?id=2");
        assertEquals(200, result.statusCode());
        assertEquals(expectedEpic1, result.body());
    }

    @Test
    void deleteEpics() {
        result = testClient.sendDelete("/tasks/epic/");
        assertEquals(200, result.statusCode());

        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        testClient.sendPost("/tasks/epic/", testString);

        result = testClient.sendDelete("/tasks/epic/");
        assertEquals(200, result.statusCode());
        assertEquals("Все эпики удалены", result.body());

        result = testClient.sendGet("/tasks/epic/?id=2");
        assertEquals("Эпика с id=2 не найдено", result.body());
    }

    @Test
    void deleteOneEpic() {
        testString = "{\"subTasksId\":[],\"title\":\"test\",\"description\":\"test\",\"id\":2}";
        testClient.sendPost("/tasks/epic/", testString);

        result = testClient.sendDelete("/tasks/epic/?id=4ggfg");
        assertEquals(400, result.statusCode());

        result = testClient.sendDelete("/tasks/epic/?id=1");
        assertEquals(400, result.statusCode());
        assertEquals("Эпика с id=1 не найдено", result.body());

        result = testClient.sendDelete("/tasks/epic/?id=2");
        assertEquals(200, result.statusCode());
        assertEquals("Эпик удален", result.body());

        result = testClient.sendGet("/tasks/epic/?id=2");
        assertEquals(400, result.statusCode());
        assertEquals("Эпика с id=2 не найдено", result.body());
    }

    /* Subtasks*/

    @Test
    void addSubtask() {
        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        result = testClient.sendPost("/tasks/subtask/", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Подзадача не добавлена. Проверьте уникальность id подзадачи, " +
                "id эпика и незанятость периода времени другими задачами.", result.body());

        testString = "incorrect subtask to ger error";
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик
        result = testClient.sendPost("/tasks/subtask/", testString);
        assertEquals(400, result.statusCode());

        result = testClient.sendGet("/tasks/subtask/");
        assertEquals("[]", result.body());

        testString = "{\n" +
                "  \"id\": 5,\n" +
                "  \"title\": \"test\",\n" +
                "  \"description\": \"test\",\n" +
                "\t\"epicId\": 2,\n" +
                "\t\"status\": \"NEW\",\n" +
                "  \"duration\": 30,\n" +
                "  \"startTime\": \"2014-8-10-12-20\"\n" +
                "}";
        String expectedSubtask = "{\"epicId\":2,\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\"," +
                "\"id\":5,\"startTime\":\"2014-8-10-12-20\",\"duration\":30}";
        result = testClient.sendPost("/tasks/subtask/", testString);
        assertEquals(200, result.statusCode());
        assertEquals("Подзадача добавлена", result.body());

        result = testClient.sendGet("/tasks/subtask/?id=5");
        assertEquals(expectedSubtask, result.body());

        result = testClient.sendPost("/tasks/subtask/", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Подзадача не добавлена. Проверьте уникальность id подзадачи, id эпика и незанятость " +
                "периода времени другими задачами.", result.body());
    }

    @Test
    void updateSubtask() {
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": \"NEW\", " +
                "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        result = testClient.sendPost("/tasks/subtask/", testString);

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"testtest\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        result = testClient.sendPost("/tasks/subtask/?id=5", testString);
        assertEquals(200, result.statusCode());
        assertEquals("Подзадача обновлена", result.body());

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"testtest\", \"epicId\": 2, \"status\": " +
                "\"DONE\", " + "\"duration\": 30, \"startTime\": \"2010-8-10-12-20\"}";
        result = testClient.sendPost("/tasks/subtask/?id=4", testString);
        assertEquals(400, result.statusCode());
        assertEquals("id в url и в теле не совпадают.", result.body());

        testString = "{\"id\": 4, \"title\": \"test\", \"description\": \"testtest\", \"epicId\": 3, \"status\": " +
                "\"DONE\", " + "\"duration\": 30, \"startTime\": \"2010-8-10-12-20\"}";
        result = testClient.sendPost("/tasks/subtask/?id=4", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Подзадача не обновлена. Проверьте id, id эпика и " +
                "незанятость периода времени другими задачами.", result.body());

        testString = "efwefwef";
        result = testClient.sendPost("/tasks/subtask/?id=5", testString);
        assertEquals(400, result.statusCode());
        assertEquals("Подзадача не обновлена. Проверьте корректность введённых параметров", result.body());
    }

    @Test
    void getSubtasks() {
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        testClient.sendPost("/tasks/subtask/", testString);

        String expectedSubtask = "{\"epicId\":2,\"title\":\"test\",\"description\":\"test\",\"status\":\"IN_PROGRESS\"," +
                "\"id\":5,\"startTime\":\"2014-8-10-12-20\",\"duration\":30}";
        result = testClient.sendGet("/tasks/subtask/");
        assertEquals(200, result.statusCode());
        assertEquals("["+expectedSubtask+"]", result.body());

        testString1 = "{\"id\": 6, \"title\": \"test\", \"description\": \"testtest\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2013-8-10-12-20\"}";
        String expectedSubtask1 = "{\"epicId\":2,\"title\":\"test\",\"description\":\"testtest\",\"status\":" +
                "\"IN_PROGRESS\",\"id\":6,\"startTime\":\"2013-8-10-12-20\",\"duration\":30}";
        testClient.sendPost("/tasks/subtask/", testString1);

        result = testClient.sendGet("/tasks/subtask/");
        assertEquals(200, result.statusCode());
        assertEquals("["+expectedSubtask+","+expectedSubtask1+"]", result.body());
    }

    @Test
    void getOneSubtask() {
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        testClient.sendPost("/tasks/subtask/", testString);

        result = testClient.sendGet("/tasks/subtask/?id=4ggfg");
        assertEquals(400, result.statusCode());
        assertEquals("id подзадачи передан неверно", result.body());

        result = testClient.sendGet("/tasks/subtask/?id=1");
        assertEquals(400, result.statusCode());
        assertEquals("Подзадачи с id=1 не найдено", result.body());

        result = testClient.sendGet("/tasks/subtask/?id=5");
        assertEquals(200, result.statusCode());
        assertEquals("{\"epicId\":2,\"title\":\"test\",\"description\":\"test\",\"status\":\"IN_PROGRESS\"," +
                "\"id\":5,\"startTime\":\"2014-8-10-12-20\",\"duration\":30}", result.body());
    }

    @Test
    void deleteSubtasks() {
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик

        result = testClient.sendDelete("/tasks/subtask/");
        assertEquals(200, result.statusCode());

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        testClient.sendPost("/tasks/subtask/", testString);

        result = testClient.sendDelete("/tasks/subtask/");
        assertEquals(200, result.statusCode());
        assertEquals("Все подзадачи удалены", result.body());

        result = testClient.sendGet("/tasks/subtask/");
        assertEquals("[]", result.body());
    }

    @Test
    void deleteOneSubtask() {
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик

        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        testClient.sendPost("/tasks/subtask/", testString);

        result = testClient.sendDelete("/tasks/subtask/?id=4ggfg");
        assertEquals(400, result.statusCode());

        result = testClient.sendDelete("/tasks/subtask/?id=1");
        assertEquals(400, result.statusCode());

        result = testClient.sendDelete("/tasks/subtask/?id=5");
        assertEquals(200, result.statusCode());
        assertEquals("Подзадача удалена", result.body());

        result = testClient.sendGet("/tasks/subtask/?id=5");
        assertEquals(400, result.statusCode());
        assertEquals("Подзадачи с id=5 не найдено", result.body());
    }

    /* history */

    @Test
    void getHistory() {
        result = testClient.sendGet("/tasks/history/");
        assertEquals(200, result.statusCode());
        assertEquals("[]", result.body());

        testString = "{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4,\"startTime\":\"2010-6-10-12-20\",\"duration\":20}";
        result = testClient.sendPost("/tasks/task/", testString);
        testClient.sendPost("/tasks/epic/", "{\"subTasksId\":[],\"title\":\"test\"," +
                "\"description\":\"test\",\"id\":2}"); // Добавим эпик
        testString = "{\"id\": 5, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2014-8-10-12-20\"}";
        testClient.sendPost("/tasks/subtask/", testString);
        testString1 = "{\"id\": 6, \"title\": \"test\", \"description\": \"test\", \"epicId\": 2, \"status\": " +
                "\"IN_PROGRESS\", " + "\"duration\": 30, \"startTime\": \"2013-8-10-12-20\"}";
        testClient.sendPost("/tasks/subtask/", testString1);

        testClient.sendGet("/tasks/task/?id=4");
        testClient.sendGet("/tasks/subtask/?id=6");
        testClient.sendGet("/tasks/task/?id=4");
        testClient.sendGet("/tasks/subtask/?id=6");

        String expectedHistory = "[{\"title\":\"test\",\"description\":\"test\",\"status\":\"NEW\",\"id\":4," +
                "\"startTime\":\"2010-6-10-12-20\",\"duration\":20}," +
                "{\"epicId\":2,\"title\":\"test\",\"description\":\"test\",\"status\":\"IN_PROGRESS\",\"id\":6," +
                "\"startTime\":\"2013-8-10-12-20\",\"duration\":30}]";

        result = testClient.sendGet("/tasks/history/");

        assertEquals(200, result.statusCode());
        assertEquals(expectedHistory, result.body());
    }
}
