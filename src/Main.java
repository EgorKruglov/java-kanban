import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;

public class Main {

/*    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }*/

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }

/*    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
        new KVServer().start();
    }*/
}
