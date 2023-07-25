package manager;

import server.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {

    KVTaskClient client;

    public HttpTaskManager(String path) throws IOException, InterruptedException {
        client = new KVTaskClient(path);
    }

    /* Я не до конца понял, как работает этот класс.
    При сохранении нужно отдельно каждую задачу передавать и в ключе её id?
    Или нужно всё состояние приложения целиком отправлять? Тогда что такое ключ?
    Извините, что иногда кидаю сильно не доделанную работу. Очень хочется, чтобы на половине пути кто-то взглянул
    и поправил, если делаю что-то сильно не так.*/

}
