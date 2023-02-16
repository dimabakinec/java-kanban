
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kanban.managers.taskManagers.InMemoryTasksManager;
import kanban.managers.taskManagers.TasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import kanban.tasks.enums.TaskType;
import kanban.managers.Managers;
import kanban.utils.Formatter;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.Epic;

import kanban.servers.HttpTasksServer;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTasksServerTest {

    private HttpTasksServer server;
    private Subtask subtask1;
    private Subtask subtask2;
    private Task task1;
    private Epic epic1;
    private Gson gson;

    @BeforeEach
    void loadInitialConditions() throws IOException {

        TasksManager manager = new InMemoryTasksManager();
        server = new HttpTasksServer(manager);
        gson = Formatter.createGson();

        task1 = manager.createTask(
                new Task("Task1", "Task1D", Instant.ofEpochSecond(5000), 5));

        epic1 = manager.createEpic(
                new Epic("Epic1", "Epic1D", TaskType.EPIC));

        subtask1 = manager.createSubtask(
                new Subtask("Subtask1", "Subtask1D", Instant.EPOCH, 50, epic1.getId()));

        subtask2 = manager.createSubtask(
                new Subtask("Subtask2", "Subtask2D", Instant.ofEpochSecond(11111), 50, epic1.getId()));

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());

        server.start();

    }

    @AfterEach
    void serverStop() {

        server.stop();

    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> tasks = gson.fromJson(response.body(), type);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());

    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/epic");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<Map<Integer, Epic>>(){}.getType();
        Map<Integer, Epic> epics = gson.fromJson(response.body(), type);

        assertNotNull(epics);
        assertEquals(1, epics.size());

    }

    @Test
    void getSubtasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/subtask");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<Map<Integer, Subtask>>(){}.getType();
        Map<Integer, Subtask> subtasks = gson.fromJson(response.body(), type);

        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());

    }

    @Test
    void getTaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task/1");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<Task>(){}.getType();
        Task task = gson.fromJson(response.body(), type);

        assertNotNull(task);
        assertEquals(task1, task);

    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/history");

        var request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        assertNotNull(tasks);
        assertEquals(4, tasks.size());

    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        assertNotNull(tasks);
        assertEquals(3, tasks.size());

    }

    @Test
    void createTaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task");

        var json = gson.toJson(task1);

        var body = HttpRequest.BodyPublishers.ofString(json);

        var request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/epic");

        var json = gson.toJson(epic1);

        var body = HttpRequest.BodyPublishers.ofString(json);

        var request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

    @Test
    void createSubtaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/subtask");

        var json = gson.toJson(subtask1);

        var body = HttpRequest.BodyPublishers.ofString(json);

        var request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

    @Test
    void deleteTaskTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task/1");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

    @Test
    void deleteTasksTest() throws IOException, InterruptedException {

        var client = HttpClient.newHttpClient();
        var url = URI.create("http://localhost:8080/tasks/task");

        var request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        var type = new TypeToken<Map<Integer, Task>>(){}.getType();
        Map<Integer, Task> tasks = gson.fromJson(response.body(), type);

        assertNull(tasks);

    }

}