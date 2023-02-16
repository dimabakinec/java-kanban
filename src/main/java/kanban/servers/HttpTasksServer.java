package kanban.servers;

import kanban.managers.taskManagers.TasksManager;
import kanban.servers.handlers.HomepageHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import kanban.utils.Formatter;
import com.google.gson.Gson;
import kanban.tasks.Subtask;
import kanban.tasks.Epic;
import kanban.tasks.Task;

import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HttpTasksServer {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private byte[] response = new byte[0];
    private static final int PORT = 8080;
    private final TasksManager manager;
    private HttpExchange httpExchange;
    private final HttpServer server;
    private int responseCode = 404;
    private final Gson gson;
    private String getPath;

    public HttpTasksServer(TasksManager manager) throws IOException {

        this.manager = manager;

        server = HttpServer.create();

        server.bind(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHandler());
        server.createContext("/", new HomepageHandler());

        gson = Formatter.createGson();

    }

    public void start() {

        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }

    public void stop() {

        server.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");

    }


    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {

            ArrayList<String> regexRoots = new ArrayList<>();
            regexRoots.add("^/tasks$");
            regexRoots.add("^/tasks/task$");
            regexRoots.add("^/tasks/epic$");
            regexRoots.add("^/tasks/subtask$");
            regexRoots.add("^/tasks/task/\\d+$");
            regexRoots.add("^/tasks/history$");
            regexRoots.add("^/tasks/subtask/epic/\\d+$");
            regexRoots.add("^/tasks/task/\\d+$");

            var path = exchange.getRequestURI().getPath();
            var method = exchange.getRequestMethod();
            httpExchange = exchange;
            getPath = path;

            try {
                switch (method) {
                    case "GET":
                        if (Pattern.matches(regexRoots.get(0), path))
                            getPrioritizedTasks();
                        if (Pattern.matches(regexRoots.get(1), path))
                            getTasks();
                        if (Pattern.matches(regexRoots.get(2), path))
                            getEpics();
                        if (Pattern.matches(regexRoots.get(3), path))
                            getSubtasks();
                        if (Pattern.matches(regexRoots.get(4), path))
                            getTasksByID();
                        if (Pattern.matches(regexRoots.get(5), path))
                            getHistory();
                        if (Pattern.matches(regexRoots.get(6), path))
                            getSubtasksByID();
                        break;

                    case "POST":
                        if (Pattern.matches(regexRoots.get(1), path))
                            createTask();
                        if (Pattern.matches(regexRoots.get(2), path))
                            createEpic();
                        if (Pattern.matches(regexRoots.get(3), path))
                            createSubtask();
                        break;

                    case "DELETE":
                        if (Pattern.matches(regexRoots.get(1), path))
                            deleteAllTasksEpicsSubtasks();
                        if (Pattern.matches(regexRoots.get(7), path))
                            deleteByID();
                        break;

                    default:
                        System.out.println("Метод " + method + " не поддерживается.");
                }
                exchange.sendResponseHeaders(responseCode, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            } catch (IOException e) {
                System.out.println("Ошибка выполнения запроса: " + e.getMessage());
            } finally {
                exchange.close();
            }
        }

        private void getPrioritizedTasks() {
            System.out.println("GET: началась обработка /tasks запроса от клиента.\n");
            var prioritizedTasksToJson = gson.toJson(manager.getPrioritizedTasks());

            if (!prioritizedTasksToJson.isEmpty()) {
                response = prioritizedTasksToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void getTasks() {
            System.out.println("GET: началась обработка /tasks/task запроса от клиента.\n");
            var tasksToJson = gson.toJson(manager.getTasks());

            if (!tasksToJson.isEmpty()) {
                response = tasksToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void getEpics() {
            System.out.println("GET: началась обработка /tasks/epic запроса от клиента.\n");
            var epicsToJson = gson.toJson(manager.getEpics());

            if (!epicsToJson.isEmpty()) {
                response = epicsToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void getSubtasks() {
            System.out.println("GET: началась обработка /tasks/subtask запроса от клиента.\n");
            var subtasksToJson = gson.toJson(manager.getSubtasks());

            if (!subtasksToJson.isEmpty()) {
                response = subtasksToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void getTasksByID() {
            System.out.println("GET: началась обработка /tasks/task/d+ запроса от клиента.\n");
            int id = Integer.parseInt(getPath.replaceFirst("/tasks/task/", ""));
            var taskByIDToJson = gson.toJson(manager.getTasks().get(id));

            if (!taskByIDToJson.isEmpty()) {
                response = taskByIDToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void getHistory() {
            System.out.println("GET: началась обработка /tasks/history запроса от клиента.\n");
            var historyToJson = gson.toJson(manager.getHistory());

            if (!historyToJson.isEmpty()) {
                response = historyToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void getSubtasksByID() {
            System.out.println("GET: началась обработка /tasks/subtask/epic/d+ запроса от клиента.\n");
            int id = Integer.parseInt(getPath.replaceFirst("/tasks/subtask/epic/", ""));
            var subtaskEpicToJson = gson.toJson(manager.getSubtasks().get(id));

            if (!subtaskEpicToJson.isEmpty()) {
                response = subtaskEpicToJson.getBytes(DEFAULT_CHARSET);
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                responseCode = 200;
            } else
                responseCode = 400;
        }

        private void createTask() throws IOException {
            System.out.println("POST: началась обработка /tasks/task запроса от клиента.\n");
            var inputStream = httpExchange.getRequestBody();
            var body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            var task = gson.fromJson(body, Task.class);
            responseCode = 200;

            if (!manager.getTasks().containsKey(task.getId())) {
                manager.createTask(task);
                System.out.println("Задача #" + task.getId() + " создана.\n" + body);
            } else {
                manager.update(task);
                System.out.println("Задача #" + task.getId() + " обновлена.\n" + body);
            }
        }

        private void createEpic() throws IOException {
            System.out.println("POST: началась обработка /tasks/epic запроса от клиента.\n");
            var inputStream = httpExchange.getRequestBody();
            var body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            var epic = gson.fromJson(body, Epic.class);
            responseCode = 200;

            if (!manager.getEpics().containsKey(epic.getId())) {
                manager.createEpic(epic);
                System.out.println("Задача #" + epic.getId() + " создана.\n" + body);
            } else {
                manager.update(epic);
                System.out.println("Задача #" + epic.getId() + " обновлена.\n" + body);
            }
        }

        private void createSubtask() throws IOException {
            System.out.println("POST: началась обработка /tasks/subtask запроса от клиента.\n");
            var inputStream = httpExchange.getRequestBody();
            var body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            var subtask = gson.fromJson(body, Subtask.class);
            responseCode = 200;

            if (!manager.getSubtasks().containsKey(subtask.getId())) {
                manager.createSubtask(subtask);
                System.out.println("Задача #" + subtask.getId() + " создана.\n" + body);
            } else {
                manager.update(subtask);
                System.out.println("Задача #" + subtask.getId() + " создана.\n" + body);
            }
        }

        private void deleteAllTasksEpicsSubtasks() {
            System.out.println("DELETE: началась обработка /tasks/task запроса от клиента.\n");
            manager.removeAllTasksEpicsSubtasks();
            System.out.println("Все таски, эпики и сабтаски удалены.");
            responseCode = 200;
        }

        private void deleteByID() {
            System.out.println("DELETE: началась обработка /tasks/task/d+ запроса от клиента.\n");
            int id = Integer.parseInt(getPath.replaceFirst("/tasks/task/", ""));
            if (manager.getTasks().containsKey(id)) {
                manager.removeTask(id);
                System.out.println("Задача #" + id + " удалена.\n");
            }
            if (manager.getEpics().containsKey(id)) {
                manager.removeEpic(id);
                System.out.println("Задача #" + id + " удалена.\n");
            }
            if (manager.getSubtasks().containsKey(id)) {
                manager.removeSubtask(id);
                System.out.println("Задача #" + id + " удалена.\n");
            }
            responseCode = 200;
        }
    }
}