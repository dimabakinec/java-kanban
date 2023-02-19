package ru.yandex.practicum.tasktracker.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String responseBody; // тело ответа которое выдается при регистрации.
    private final URI url; // URL к серверу хранилища
    private final HttpClient client;
    private HttpResponse<String> response;

    public KVTaskClient(URI url) throws IOException, InterruptedException {
        this.url = url;

        // HTTP-клиент с настройками по умолчанию
        client = HttpClient.newHttpClient();
        register();
    }

    private void checkResponseCode(HttpResponse<String> response) { // Метод проверяет код ответа.
        int statusCode = response.statusCode();
        if (statusCode != 200 && statusCode != 204) {
            System.out.println("Сервер сообщил о проблеме с запросом. Код состояния: " + statusCode );
            System.out.println(response.body());
        }
    }

    private void register() {
        try {
            // создаём экземпляр URI, содержащий адрес нужного ресурса
            URI uri = URI.create(url + "/register");

            // создаём объект, описывающий HTTP-запрос
            HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                    .uri(uri) // указываем адрес ресурса
                    .GET()    // указываем HTTP-метод запроса
                    .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                    .header("Content-type", "application/json") // название заголовка и его значение
                    .build(); // заканчиваем настройку и создаём ("строим") http-запрос

            // отправляем запрос и получаем HTTP-ответ от сервера
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponseCode(response);

            // получаем тело ответа в виде строки - токен (API_TOKEN)
            responseBody = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Не удалось выполнить запрос. Ошибка при регистрации: " + e.getMessage());
        }
    }

    // сохраняем состояние менеджера задач через запрос с методом POST
    public void put(String key, String json) {
        URI newUri = URI.create(url + "/save/" + key + "?API_TOKEN=" + responseBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(newUri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponseCode(response);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + newUri + "', возникла ошибка.\n"
                    + "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    // возвращаем состояние менеджера задач через запрос с методом GET
    public String load(String key) {
        URI newUrl = URI.create(url + "/load/" + key + "?API_TOKEN=" + responseBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(newUrl)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-type", "application/json")
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponseCode(response);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + newUrl + "', возникла ошибка.\n"
                    + e.getMessage());
        }
        return response.body();
    }
}