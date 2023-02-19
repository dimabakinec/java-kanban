package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.adapter.GsonConverter;
import ru.yandex.practicum.tasktracker.service.ManagerException;
import ru.yandex.practicum.tasktracker.service.Managers;
import ru.yandex.practicum.tasktracker.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskServer {
    public static final int PORT = 8080;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    private final Gson gson = GsonConverter.getGsonTaskConverter();
    private final TaskManager taskManager;

    public HttpTaskServer() {
        try {
            this.taskManager = Managers.getDefaultHttpTaskManager(); // Реализуем новый менеджер через Managers.
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.createContext("/tasks/task", new TaskHandler(taskManager)); // Связываем путь и обработчик запросов.
            server.createContext("/tasks/history", this::handleTasksOrHistory);
            server.createContext("/tasks", this::handleTasksOrHistory);
            server.createContext("/tasks/epic", new EpicHandler(taskManager));
            server.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        } catch (IOException e) {
            throw new ManagerException("Ошибка при подключении к HttpTaskServer");
        }
    }

    // Обработчик по запросу истории просмотров и списка задач в порядке приоритета.
    private void handleTasksOrHistory(HttpExchange httpExchange) {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            if(method.equals("GET") && Pattern.matches("^/tasks$", path)) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(httpExchange, response, 200);
                return;

            }
            if(method.equals("GET") && Pattern.matches("^/tasks/history$", path)) {
                String response = gson.toJson(taskManager.getHistory());
                sendText(httpExchange, response, 200);
                return;
            }

            String text = "Во время выполнения запроса ресурса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес, метод (ожидали GET, а получили - " + method + ") \n" +
                    "и повторите попытку.";
            sendText(httpExchange, text, 405);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);

    }

    // Ответ на запрос.
    protected static void sendText(HttpExchange h, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(responseCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}