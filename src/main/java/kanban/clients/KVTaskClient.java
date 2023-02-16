package kanban.clients;

import kanban.utils.Formatter;

import java.nio.charset.StandardCharsets;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.io.IOException;
import java.net.URI;


public class KVTaskClient {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final int PORT = 8078;
    private final String url;
    private String token;

    public KVTaskClient() {

        url = "http://localhost:" + PORT;
        token = registerAPIToken(url);
        Formatter.createGson();

    }

    private String registerAPIToken(String url) {

        var client = HttpClient.newHttpClient();

        var uri = URI.create(url + "/register");

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            token = response.body();

        } catch (InterruptedException | IOException e) {

            System.out.println("Регистрация API токена закончилась неудачно. Причина:" + e.getMessage());

        }

        return token;

    }

    public void save(String key, String value) {

        var client = HttpClient.newHttpClient();

        var uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + token);

        var body = HttpRequest.BodyPublishers.ofString(value, DEFAULT_CHARSET);

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();

        try {

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException | IOException e) {

            System.out.println("Сохранение закончилось неудачно. Причина:" + e.getMessage());

        }

    }

    public String load(String key) {

        var client = HttpClient.newHttpClient();

        var uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + token);

        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = null;

        try {

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException | IOException e) {

            System.out.println("Загрузка закончилась неудачно. Причина:" + e.getMessage());

        }

        return response != null ? response.body() : "KVTaskClient.load() is greeting you";

    }

}