package server.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String uri;
    private final HttpClient client;
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final String apiToken;

    public KVTaskClient(String uri) throws IOException, InterruptedException {
        this.uri = uri;
        client  = HttpClient.newHttpClient();
        this.apiToken = getApiToken();
    }

    public String load(String key) throws IOException, InterruptedException {
        URI loadUri = URI.create(uri + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(loadUri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() != 200) {
            System.out.println("Не удалось получить сохранённые данные");
            return null;
        }
        return response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI putUri = URI.create(uri + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(putUri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() != 200) {
            System.out.println("Не удалось сохранить данные");
            return;
        }
    }

    private String getApiToken() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/register"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        if (response.statusCode() != 200) {
            System.out.println("Не удалось получить токен");
            return null;
        }
        System.out.println("Регистрация успешна. Api Токен: " + response.body());
        return response.body();
    }
}
