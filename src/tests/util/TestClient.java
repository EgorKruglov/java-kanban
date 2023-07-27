package tests.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/* Это класс, который может кидать запросы на HttpTaskServer */
public class TestClient {
    private final String uri;
    private final HttpClient client;
    private final HttpResponse.BodyHandler<String> handler;

    public TestClient(String uri) {
        this.uri = uri;
        client = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
    }

    public HttpResponse<String> sendGet(String endOfUri) {
        URI loadUri = URI.create(uri + endOfUri);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(loadUri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        return getResponse(request);
    }

    public HttpResponse<String> sendPost(String endOfUri, String jsonBody) {
        URI loadUri = URI.create(uri + endOfUri);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(loadUri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        return getResponse(request);
    }

    public HttpResponse<String> sendDelete(String endOfUri) {
        URI loadUri = URI.create(uri + endOfUri);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(loadUri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        return getResponse(request);
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, handler);
            return response;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
