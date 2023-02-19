package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasktracker.adapter.GsonConverter;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.service.ManagerException;
import ru.yandex.practicum.tasktracker.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

import static ru.yandex.practicum.tasktracker.server.HttpTaskServer.DEFAULT_CHARSET;
import static ru.yandex.practicum.tasktracker.server.HttpTaskServer.sendText;

public class SubtaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonConverter.getGsonTaskConverter();

    public SubtaskHandler(TaskManager taskManager) {
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
                case GET_ALL: {
                    String response = gson.toJson(taskManager.getListedOfAllSubtasks());
                    sendText(httpExchange, response, 200);
                    break;
                }
                case GET_ID: {
                    String pathId = getPathID(path);
                    int id = getId(pathId);
                    if (id != -1) {
                        String response = gson.toJson(taskManager.getByIDSubtask(id));
                        sendText(httpExchange, response, 200);
                    } else {
                        sendText(httpExchange, "Получен некорректный идентификатор" + pathId
                                , 400);
                    }

                    break;
                }
                case GET_EPIC_SUBTASK: {
                    String pathId = path.split("/")[4];
                    int id = getId(pathId);
                    if (id != -1) {
                        String response = gson.toJson(taskManager.getListAllSubtasksOfEpic(id));
                        sendText(httpExchange, response, 200);
                    } else {
                        sendText(httpExchange, "Получен некорректный идентификатор" + pathId
                                , 400);
                    }

                    break;
                }
                case ADD: {
                    Subtask subtask = deserializationSubtask(httpExchange);
                    if (subtask == null) return;
                    try {
                        taskManager.createSubtask(subtask);
                    } catch (ManagerException exception) {
                        sendText(httpExchange, exception.getMessage(), 400);
                    }
                    sendText(httpExchange, gson.toJson(subtask), 200);
                    break;
                }
                case UPDATE: {
                    String pathId = getPathID(path);
                    int id = getId(pathId);
                    if (id != -1) {
                        Subtask subtask = deserializationSubtask(httpExchange);
                        try {
                            taskManager.updateSubtask(subtask);
                        } catch (ManagerException exception) {
                            sendText(httpExchange, exception.getMessage(), 400);
                        }
                        sendText(httpExchange, gson.toJson(subtask), 200);
                    } else {
                        sendText(httpExchange, "Получен некорректный идентификатор" + pathId
                                , 400);
                    }
                    break;
                }
                case DELETE_ALL: { // Удаляем все задачи.
                    taskManager.deleteAllSubtasks();
                    sendText(httpExchange, "Подзадачи удалены", 200);
                    break;
                }
                case DELETE_ID: {
                    String pathId = getPathID(path);
                    int id = getId(pathId);
                    if (id != -1) {
                        try {
                            taskManager.deleteSubtaskByID(id);
                        } catch (ManagerException exception) {
                            sendText(httpExchange, exception.getMessage(), 400);
                        }

                        sendText(httpExchange, "Подзадача с ИД - " + id + "удалена", 200);
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
                if (Pattern.matches("^/tasks/subtask$", path)) {
                    return Endpoint.GET_ALL;
                }
                if (Pattern.matches("^/tasks/subtask/\\d+$", path)) {
                    return Endpoint.GET_ID;
                }
                if (Pattern.matches("^/tasks/subtask/epic/\\d+$", path)) {
                    return Endpoint.GET_EPIC_SUBTASK;
                }
                break;
            case "POST":
                if (Pattern.matches("^/tasks/subtask$", path)) {
                    return Endpoint.ADD;
                }
                if (Pattern.matches("^/tasks/subtask/\\d+$", path)) {
                    return Endpoint.UPDATE;
                }
                break;
            case "DELETE":
                if (Pattern.matches("^/tasks/subtask$", path)) {
                    return Endpoint.DELETE_ALL;
                }
                if (Pattern.matches("^/tasks/subtask/\\d+$", path)) {
                    return Endpoint.DELETE_ID;
                }
                break;
        }
        return Endpoint.UNKNOWN;
    }

    protected Subtask deserializationSubtask(HttpExchange httpExchange) throws IOException {
        try {
            // Извлекаем тело запроса.
            String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            final JsonElement jsonElement = JsonParser.parseString(body);
            try {
                return gson.fromJson(jsonElement, Subtask.class);
            } catch (JsonSyntaxException e) {
                sendText(httpExchange, "Получен некорректный JSON", 400);
            }
        } catch (IOException exception) {
            sendText(httpExchange, exception.getMessage(), 400);
        }

        return null;
    }

    private String getPathID(String path) { // ИД который передан в элементе URL
        return path.split("/")[3];
    }

    protected int getId(String pathId) { // Форматируем ИД, если формат не верный вернет -1.
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}