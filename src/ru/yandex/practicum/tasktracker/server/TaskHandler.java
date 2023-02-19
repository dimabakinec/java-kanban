package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasktracker.adapter.GsonConverter;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.service.ManagerException;
import ru.yandex.practicum.tasktracker.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static ru.yandex.practicum.tasktracker.server.HttpTaskServer.DEFAULT_CHARSET;
import static ru.yandex.practicum.tasktracker.server.HttpTaskServer.sendText;

public class TaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonConverter.getGsonTaskConverter();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    //В этом методе будет находиться код, который определяет логику работы эндпоинтов.
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(path, method);

            switch (endpoint) {
                case GET_ALL: { // Запрос всех задач.
                    String response = gson.toJson(taskManager.getListedOfAllTasks());
                    sendText(httpExchange, response, 200);
                    break;
                }
                case GET_ID: { //Запрос задачи по ИД.
                    String pathId = getPathID(path);
                    int id = getId(pathId);
                    if (id != -1) {
                        String response = gson.toJson(taskManager.getByIDTask(id));
                        sendText(httpExchange, response, 200);
                    } else {
                        sendText(httpExchange, "Получен некорректный идентификатор" + pathId
                                , 400);
                    }

                    break;
                }
                case ADD: { // Добавляем задачу.
                    Task task = deserializationTasks(httpExchange);
                    if (task == null) return;
                    try {
                        taskManager.createTask(task);
                    } catch (ManagerException exception) {
                        sendText(httpExchange, exception.getMessage(), 400);
                    }
                    sendText(httpExchange, gson.toJson(task), 200);
                    break;
                }
                case UPDATE: { // Обновляем задачу.
                    String pathId = getPathID(path);
                    int id = getId(pathId);
                    if (id != -1) {
                        Task task = deserializationTasks(httpExchange);
                        try {
                            taskManager.updateTask(task);
                        } catch (ManagerException exception) {
                            sendText(httpExchange, exception.getMessage(), 400);
                        }
                        sendText(httpExchange, gson.toJson(task), 200);
                    } else {
                        sendText(httpExchange, "Получен некорректный идентификатор" + pathId
                                , 400);
                    }
                    break;
                }
                case DELETE_ALL: { // Удаляем все задачи.
                    taskManager.deleteAllTasks();
                    sendText(httpExchange, "Задачи удалены", 200);
                    break;
                }
                case DELETE_ID: { // Удаляем задачу по ИД.
                    String pathId = getPathID(path);
                    int id = getId(pathId);
                    if (id != -1) {
                        try {
                            taskManager.deleteTaskByID(id);
                        } catch (ManagerException exception) {
                            sendText(httpExchange, exception.getMessage(), 400);
                        }

                        sendText(httpExchange, "Задача с ИД - " + id + "удалена", 200);
                    } else {
                        sendText(httpExchange, "Получен некорректный идентификатор" + pathId
                                , 400);
                    }
                    break;
                }
                default:
                    String text = "Получен не допустимый метод запроса. Ожидалось GET, POST или DELETE";
                    sendText(httpExchange, text, 400);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private Endpoint getEndpoint(String path, String method) {
        switch (method) {
            case "GET":
                if (Pattern.matches("^/tasks/task$", path)) {
                    return Endpoint.GET_ALL;
                }
                if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                    return Endpoint.GET_ID;
                }
                break;
            case "POST":
                if (Pattern.matches("^/tasks/task$", path)) {
                    return Endpoint.ADD;
                }
                if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                    return Endpoint.UPDATE;
                }
                break;
            case "DELETE":
                if (Pattern.matches("^/tasks/task$", path)) {
                    return Endpoint.DELETE_ALL;
                }
                if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                    return Endpoint.DELETE_ID;
                }
                break;
        }
        return Endpoint.UNKNOWN;
    }

    protected Task deserializationTasks(HttpExchange httpExchange) throws IOException {
        try {
            // Извлекаем тело запроса.
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            final JsonElement jsonElement = JsonParser.parseString(body);
            try {
                return gson.fromJson(jsonElement, Task.class);
            } catch (JsonSyntaxException e) {
                sendText(httpExchange, "Получен некорректный JSON", 400);
                e.printStackTrace();
            }
        } catch (IOException exception) {
            sendText(httpExchange, exception.getMessage(), 400);
        }

        return null;
    }

    private String getPathID(String path) { // ИД который передан в элементе URL
        return path.split("/")[3];
    }

    private int getId(String pathId) { // Форматируем ИД, если формат не верный вернет -1.
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}