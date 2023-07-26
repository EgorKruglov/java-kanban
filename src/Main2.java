import server.HttpTaskServer;

import java.io.IOException;

public class Main2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        /* Чтобы тестировать взаимодействие клиент-HttpTaskServer-KVServer я запускал HttpTaskServer и KVServer
        в двух разных проектах ItellejIdea. */
        new HttpTaskServer().start();
    }
}
