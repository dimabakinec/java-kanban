package ru.yandex.practicum.tasktracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.tasktracker.adapter.GsonConverter;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.server.HttpTaskServer;
import ru.yandex.practicum.tasktracker.server.KVServer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.tasktracker.model.TaskStatus.IN_PROGRESS;

class HttpTaskManagerTest {

    private final Gson gson = GsonConverter.getGsonTaskConverter();
    private final HttpClient client = HttpClient.newHttpClient();
    static HttpTaskServer httpTaskServer;
    static KVServer kvServer;
    private Task newTask;
    private Task newTask1;
    private Task newTask2;
    private Epic newEpic;
    private Epic newEpic1;
    private Epic newEpic2;
    private Subtask newSubtask;
    private Subtask newSubtask1;
    private Subtask newSubtask2;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        newTask = new Task("Задача №2", "Описание задачи №2"
                , 0, null);
        newTask1 = new Task(1, "Задача №1", IN_PROGRESS, "Описание задачи №1"
                , 50, LocalDateTime.of(2022, 12, 27, 20, 0));
        newTask2 = new Task("Задача №2", "Описание задачи №2"
                , 0, null);
        newEpic = new Epic("Эпик №1", "Описание эпика №1");
        newEpic1 = new Epic(1, "Эпик №1", "Описание эпика №1 изменено");
        newEpic2 = new Epic("Эпик №2", "Описание эпика №2");
        newSubtask = new Subtask("Подзадача №2 эпика №1", "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  1);
        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1 изменено",  50
                , LocalDateTime.of(2022, 12, 27, 21, 30), 1);
        newSubtask2 = new Subtask("Подзадача №1 эпика №1", "Описание подзадачи №1 эпика №1 изменено",  50
                , LocalDateTime.of(2022, 12, 27, 20, 0), 1);
    }

    private HttpResponse<String> responseToPOSTRequest(URI url, Task task) {
        String json = gson.toJson(task);
        HttpResponse<String> response = null;
        try {
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(body)
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Что то пошло не так!");
        }
        return response;
    }

    private HttpResponse<String> responseToGETRequest(URI url) {
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Что то пошло не так!");
        }
        return response;
    }

    private HttpResponse<String> responseToDELETERequest(URI url) {
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Что то пошло не так!");
        }
        return response;
    }

    int getId(HttpResponse<String> response) {
        int uin = 0;
        if (response.statusCode() == 200) {
            JsonElement jsonElement = JsonParser.parseString(response.body());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            uin = jsonObject.get("uin").getAsInt();
        }
        return uin;
    }

    List getListTask(HttpResponse<String> response){
        Type taskListType = new TypeToken<List<Task>>() {}.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskListType);
        return tasks;
    }

    List getListEpic(HttpResponse<String> response){
        Type epicListType = new TypeToken<List<Epic>>() {}.getType();
        List<Epic> epics = gson.fromJson(response.body(), epicListType);
        return epics;
    }

    List getListSubtask(HttpResponse<String> response){
        Type subtaskListType = new TypeToken<List<Subtask>>() {}.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), subtaskListType);
        return subtasks;
    }

    @Test
    void shouldCreateUpdateGetDeleteTaskResponseStatusCode200() {
        URI url = URI.create("http://localhost:8080/tasks/task");

        HttpResponse<String> response = responseToPOSTRequest(url, newTask); // Create Task
        assertEquals(200, response.statusCode(), "Статус ответа (задача) не 200.");

        int idTask = getId(response);
        assertEquals(1, idTask, "invalid ID TASK value");

        HttpResponse<String> responseGetAllTask = responseToGETRequest(url); // Get All Task
        List<Task> tasks = getListTask(responseGetAllTask);
        assertEquals(200, responseGetAllTask.statusCode(), "Статус ответа (getAllTasks) не 200.");
        assertEquals(1, tasks.size(), "Размер списка задач не верный.");

        URI urlIdTask = URI.create("http://localhost:8080/tasks/task/" + idTask);
        HttpResponse<String> responseGetByIdTask = responseToGETRequest(urlIdTask); // Get by Id Task
        assertEquals(200, responseGetByIdTask.statusCode(), "Статус ответа (Get by Id Task) не 200.");

        HttpResponse<String> responseGetUpdateTask = responseToPOSTRequest(urlIdTask, newTask1); // Update Task
        assertEquals(200, responseGetUpdateTask.statusCode(), "Статус ответа (Get by Id Task) не 200.");
        Type typeTask = new TypeToken<Task>() {}.getType();
        Task task = gson.fromJson(responseGetUpdateTask.body(), typeTask);
        assertEquals(newTask1, task, "Задачи не равны.");

        HttpResponse<String> responseTaskID2 = responseToPOSTRequest(url, newTask2); // Create Task ID2
        assertEquals(200, responseTaskID2.statusCode(), "Статус ответа (Get by Id2 Task) не 200.");
        Task taskID2 = gson.fromJson(responseTaskID2.body(), typeTask);
        responseGetAllTask = responseToGETRequest(url); // Get All Task
        tasks = getListTask(responseGetAllTask);
        assertEquals(2, tasks.size(), "Размер списка задач не верный (ожидалось 2).");
        assertEquals(taskID2, tasks.get(1), "Задачи не равны (ID2)." );

        HttpResponse<String> responseDeleteByIdTask = responseToDELETERequest(urlIdTask); // Delete by Id1 Task
        assertEquals(200, responseDeleteByIdTask.statusCode(), "Статус ответа (Delete Task) не 200.");
        responseGetAllTask = responseToGETRequest(url); // Get All Task
        tasks = getListTask(responseGetAllTask);
        assertEquals(1, tasks.size(), "Размер списка задач не верный (ожидалось 1).");
        assertEquals(taskID2, tasks.get(0), "Задачи не равны (ID2)." );

        HttpResponse<String> responseDeleteAllTasks = responseToDELETERequest(url); // Delete All Tasks
        assertEquals(200, responseDeleteAllTasks.statusCode(), "Статус ответа (Delete Tasks) не 200.");
        responseGetAllTask = responseToGETRequest(url); // Get All Task
        tasks = getListTask(responseGetAllTask);
        assertTrue(tasks.isEmpty(), "Список не пустой");
    }

    @Test
    void shouldCreateUpdateGetDeleteEpicResponseStatusCode200() {
        URI url = URI.create("http://localhost:8080/tasks/epic");

        HttpResponse<String> response = responseToPOSTRequest(url, newEpic); // Create Epic
        assertEquals(200, response.statusCode(), "Статус ответа (Epic) не 200.");

        int idEpic = getId(response);
        assertEquals(1, idEpic, "invalid ID EPIC value");

        HttpResponse<String> responseGetAllEpic = responseToGETRequest(url); // Get All Epic
        List epics = getListEpic(responseGetAllEpic);
        assertEquals(200, responseGetAllEpic.statusCode(), "Статус ответа (getAllEpics) не 200.");
        assertEquals(1, epics.size(), "Размер списка Epic не верный.");

        URI urlIdEpic = URI.create("http://localhost:8080/tasks/epic/" + idEpic);
        HttpResponse<String> responseGetByIdEpic = responseToGETRequest(urlIdEpic); // Get by Id Task
        assertEquals(200, responseGetByIdEpic.statusCode(), "Статус ответа (Get by Id Epic) не 200.");

        HttpResponse<String> responseGetUpdateEpic = responseToPOSTRequest(urlIdEpic, newEpic1); // Update Epic
        assertEquals(200, responseGetUpdateEpic.statusCode(), "Статус ответа (Get by Id Epic) не 200.");
        Type typeEpic = new TypeToken<Epic>() {}.getType();
        Epic epic = gson.fromJson(responseGetUpdateEpic.body(), typeEpic);
        assertEquals(newEpic1, epic, "Epic не равны.");

        HttpResponse<String> responseEpicID2 = responseToPOSTRequest(url, newEpic2); // Create Epic ID2
        assertEquals(200, responseEpicID2.statusCode(), "Статус ответа (Get by Id2 Epic) не 200.");
        Epic epicID2 = gson.fromJson(responseEpicID2.body(), typeEpic);
        responseGetAllEpic = responseToGETRequest(url); // Get All Epic
        epics = getListEpic(responseGetAllEpic);
        assertEquals(2, epics.size(), "Размер списка Epic не верный (ожидалось 2).");
        assertEquals(epicID2, epics.get(1), "Epic не равны (ID2)." );

        HttpResponse<String> responseDeleteByIdEpic = responseToDELETERequest(urlIdEpic); // Delete by Id1 Epic
        assertEquals(200, responseDeleteByIdEpic.statusCode(), "Статус ответа (Delete Epic) не 200.");
        responseGetAllEpic = responseToGETRequest(url); // Get All Task
        epics = getListEpic(responseGetAllEpic);
        assertEquals(1, epics.size(), "Размер списка Epic не верный (ожидалось 1).");
        assertEquals(epicID2, epics.get(0), "Epic не равны (ID2)." );

        HttpResponse<String> responseDeleteAllEpics = responseToDELETERequest(url); // Delete All Epics
        assertEquals(200, responseDeleteAllEpics.statusCode(), "Статус ответа (Delete Epics) не 200.");
        responseGetAllEpic = responseToGETRequest(url); // Get All Task
        epics = getListEpic(responseGetAllEpic);
        assertTrue(epics.isEmpty(), "Список не пустой");
    }

    @Test
    void shouldCreateUpdateGetDeleteSubtaskResponseStatusCode200() {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpResponse<String> responseEpic = responseToPOSTRequest(url, newEpic);
        assertEquals(200, responseEpic.statusCode(), "Статус ответа (эпик) не 200.");

        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");

        HttpResponse<String> response = responseToPOSTRequest(urlSubtask, newSubtask); // Create Subtask
        assertEquals(200, response.statusCode(), "Статус ответа (Subtask) не 200.");

        int idSubtask = getId(response);
        assertEquals(2, idSubtask, "invalid ID Subtask value");

        HttpResponse<String> responseGetAllSubtask = responseToGETRequest(urlSubtask); // Get All Subtask
        List<Subtask> subtasks = getListSubtask(responseGetAllSubtask);
        assertEquals(200, responseGetAllSubtask.statusCode(), "Статус ответа (getAllSubtasks) не 200.");
        assertEquals(1, subtasks.size(), "Размер списка Subtask не верный.");

        URI urlIdSubtask = URI.create("http://localhost:8080/tasks/subtask/" + idSubtask);
        HttpResponse<String> responseGetByIdSubtask = responseToGETRequest(urlIdSubtask); // Get by Id Subtask
        assertEquals(200, responseGetByIdSubtask.statusCode()
                , "Статус ответа (Get by Id Subtask) не 200.");

        HttpResponse<String> responseGetUpdateSubtask = responseToPOSTRequest(urlIdSubtask, newSubtask1); // Update
        assertEquals(200, responseGetUpdateSubtask.statusCode()
                , "Статус ответа (Get by Id Subtask) не 200.");
        Type typeSubtask = new TypeToken<Subtask>() {}.getType();
        Subtask subtask = gson.fromJson(responseGetUpdateSubtask.body(), typeSubtask);
        assertEquals(newSubtask1, subtask, "Subtask не равны.");

        HttpResponse<String> responseSubtaskID2 = responseToPOSTRequest(urlSubtask, newSubtask2); // Create Subtask ID2
        assertEquals(200, responseSubtaskID2.statusCode(), "Статус ответа (Get by Id2 Subtask) не 200.");
        Subtask subtaskID2 = gson.fromJson(responseSubtaskID2.body(), typeSubtask);
        responseGetAllSubtask = responseToGETRequest(urlSubtask); // Get All Subtask
        subtasks = getListSubtask(responseGetAllSubtask);
        assertEquals(2, subtasks.size(), "Размер списка Subtask не верный (ожидалось 2).");
        assertEquals(subtaskID2, subtasks.get(1), "Subtask не равны (ID2)." );

        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/1");
        HttpResponse<String> responseSubtasks = responseToGETRequest(uri); // getListAllSubtasksOfEpic
        assertEquals(200, responseSubtasks.statusCode()
                , "Статус ответа (Get Subtasks) не 200.");
        List<Subtask> subtasksList = getListSubtask(responseSubtasks);
        assertEquals(2, subtasksList.size(), "Размер списка subtasksList не верный (ожидалось 2).");

        HttpResponse<String> responseDeleteByIdSubtask = responseToDELETERequest(urlIdSubtask); // Delete by Id1 Subtask
        assertEquals(200, responseDeleteByIdSubtask.statusCode(), "Статус ответа (Delete Subtask) не 200.");
        responseGetAllSubtask = responseToGETRequest(urlSubtask); // Get All Task
        subtasks = getListSubtask(responseGetAllSubtask);
        assertEquals(1, subtasks.size(), "Размер списка Subtask не верный (ожидалось 1).");
        assertEquals(subtaskID2, subtasks.get(0), "Subtask не равны (ID2)." );

        HttpResponse<String> responseDeleteAllSubtasks = responseToDELETERequest(urlSubtask); // Delete All Subtask
        assertEquals(200, responseDeleteAllSubtasks.statusCode()
                , "Статус ответа (Delete Subtasks) не 200.");
        responseGetAllSubtask = responseToGETRequest(urlSubtask); // Get All Subtask
        subtasks = getListSubtask(responseGetAllSubtask);
        assertTrue(subtasks.isEmpty(), "Список не пустой");
    }

    @Test
    void shouldGetHistory(){
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpResponse<String> response = responseToGETRequest(url); // Get History list is Empty
        assertEquals(200, response.statusCode(), "Статус ответа (Get History list) не 200.");
        List<Task> history = getListTask(response);
        assertTrue(history.isEmpty(), "Список истории просмотров не пуст.");

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpResponse<String> responseEpic = responseToPOSTRequest(urlEpic, newEpic);
        assertEquals(200, responseEpic.statusCode(), "Статус ответа (эпик) не 200.");
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpResponse<String> responseSubtask = responseToPOSTRequest(urlSubtask, newSubtask); // Create Subtask
        assertEquals(200, responseSubtask.statusCode(), "Статус ответа (Subtask) не 200.");
        int idSubtask = getId(responseSubtask);
        assertEquals(2, idSubtask, "invalid ID Subtask value");
        URI urlIdSubtask = URI.create("http://localhost:8080/tasks/subtask/" + idSubtask);
        HttpResponse<String> responseGetByIdSubtask = responseToGETRequest(urlIdSubtask); // Get by Id Subtask
        assertEquals(200, responseGetByIdSubtask.statusCode()
                , "Статус ответа (Get by Id Subtask) не 200.");

        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        HttpResponse<String> responseTask = responseToPOSTRequest(urlTask, newTask); // Create Task
        assertEquals(200, responseTask.statusCode(), "Статус ответа (задача) не 200.");
        int idTask = getId(responseTask);
        assertEquals(3, idTask, "invalid ID TASK value");
        URI urlIdTask = URI.create("http://localhost:8080/tasks/task/" + idTask);
        HttpResponse<String> responseGetByIdTask = responseToGETRequest(urlIdTask); // Get by Id Task
        assertEquals(200, responseGetByIdTask.statusCode(), "Статус ответа (Get by Id Task) не 200.");

        response = responseToGETRequest(url); // Get History list
        assertEquals(200, response.statusCode(), "Статус ответа (Get History list) не 200.");
        history = getListTask(response);
        assertFalse(history.isEmpty(), "Список истории просмотров пуст.");
        assertEquals(2, history.size(), "Размер списка не соответствует ожидаемому");

        responseToDELETERequest(URI.create("http://localhost:8080/tasks/task")); // Delete All Tasks
        responseToDELETERequest(URI.create("http://localhost:8080/tasks/epic")); // Delete All Epics

        response = responseToGETRequest(url); // Get History list is Empty
        assertEquals(200, response.statusCode(), "Статус ответа (Get History list) не 200.");
        history = getListTask(response);
        assertTrue(history.isEmpty(), "Список истории просмотров пуст.");
    }

    @Test
    void shouldGetPrioritizedTasks(){
        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response = responseToGETRequest(url); // Get PrioritizedTasks list is Empty
        assertEquals(200, response.statusCode(), "Статус ответа (Get PrioritizedTasks list) не 200.");
        Type taskListType = new TypeToken<Set<Task>>() {}.getType();
        Set<Task> tasks = gson.fromJson(response.body(), taskListType);
        assertTrue(tasks.isEmpty(), "Список PrioritizedTasks не пуст.");

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpResponse<String> responseEpic = responseToPOSTRequest(urlEpic, newEpic);
        assertEquals(200, responseEpic.statusCode(), "Статус ответа (эпик) не 200.");
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpResponse<String> responseSubtask = responseToPOSTRequest(urlSubtask, newSubtask); // Create Subtask
        assertEquals(200, responseSubtask.statusCode(), "Статус ответа (Subtask) не 200.");

        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        HttpResponse<String> responseTask = responseToPOSTRequest(urlTask, newTask); // Create Task
        assertEquals(200, responseTask.statusCode(), "Статус ответа (задача) не 200.");

        response = responseToGETRequest(url); // Get PrioritizedTasks list is Empty
        assertEquals(200, response.statusCode(), "Статус ответа (Get PrioritizedTasks list) не 200.");
        taskListType = new TypeToken<Set<Task>>() {}.getType();
        tasks = gson.fromJson(response.body(), taskListType);
        assertFalse(tasks.isEmpty(), "Список PrioritizedTasks не пуст.");
        assertEquals(2, tasks.size(), "Размер списка не соответствует ожидаемому");

        responseToDELETERequest(URI.create("http://localhost:8080/tasks/task")); // Delete All Tasks
        responseToDELETERequest(URI.create("http://localhost:8080/tasks/epic")); // Delete All Epics

        response = responseToGETRequest(url); // Get PrioritizedTasks list is Empty
        assertEquals(200, response.statusCode(), "Статус ответа (Get PrioritizedTasks list) не 200.");
        taskListType = new TypeToken<Set<Task>>() {}.getType();
        tasks = gson.fromJson(response.body(), taskListType);
        assertTrue(tasks.isEmpty(), "Список PrioritizedTasks не пуст.");
    }

    // Проверяем сохранение и восстановление задач на KVservere.
    @Test
    void shouldSaveAndAfterLoadTasksIfKVServer(){
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");

        HttpResponse<String> responseGetAllTask = responseToGETRequest(urlTask); // Get All Task
        List<Task> tasks = getListTask(responseGetAllTask);
        assertEquals(200, responseGetAllTask.statusCode(), "Статус ответа (getAllTasks) не 200.");
        assertTrue(tasks.isEmpty(), "Список не пуст.");

        HttpResponse<String> responseGetAllEpic = responseToGETRequest(urlEpic); // Get All Epic
        List epics = getListEpic(responseGetAllEpic);
        assertEquals(200, responseGetAllEpic.statusCode(), "Статус ответа (getAllEpics) не 200.");
        assertTrue(epics.isEmpty(), "Список не пуст.");

        HttpResponse<String> responseGetAllSubtask = responseToGETRequest(urlSubtask); // Get All Subtask
        List<Subtask> subtasks = getListSubtask(responseGetAllSubtask);
        assertEquals(200, responseGetAllSubtask.statusCode(), "Статус ответа (getAllSubtasks) не 200.");
        assertTrue(subtasks.isEmpty(), "Список не пуст.");

        HttpResponse<String> responseEpic = responseToPOSTRequest(urlEpic, newEpic);
        assertEquals(200, responseEpic.statusCode(), "Статус ответа (эпик) не 200.");

        HttpResponse<String> responseSubtask = responseToPOSTRequest(urlSubtask, newSubtask); // Create Subtask
        assertEquals(200, responseSubtask.statusCode(), "Статус ответа (Subtask) не 200.");
        int idSubtask = getId(responseSubtask);
        assertEquals(2, idSubtask, "invalid ID Subtask value");
        URI urlIdSubtask = URI.create("http://localhost:8080/tasks/subtask/" + idSubtask);
        HttpResponse<String> responseGetByIdSubtask = responseToGETRequest(urlIdSubtask); // Get by Id Subtask
        assertEquals(200, responseGetByIdSubtask.statusCode()
                , "Статус ответа (Get by Id Subtask) не 200.");

        HttpResponse<String> responseTask = responseToPOSTRequest(urlTask, newTask); // Create Task
        assertEquals(200, responseTask.statusCode(), "Статус ответа (задача) не 200.");
        int idTask = getId(responseTask);
        assertEquals(3, idTask, "invalid ID TASK value");
        URI urlIdTask = URI.create("http://localhost:8080/tasks/task/" + idTask);
        HttpResponse<String> responseGetByIdTask = responseToGETRequest(urlIdTask); // Get by Id Task
        assertEquals(200, responseGetByIdTask.statusCode(), "Статус ответа (Get by Id Task) не 200.");

        responseGetAllTask = responseToGETRequest(urlTask); // Get All Task
        tasks = getListTask(responseGetAllTask);
        assertEquals(200, responseGetAllTask.statusCode(), "Статус ответа (getAllTasks) не 200.");
        assertEquals(1, tasks.size(), "Размер списка задач не верный.");

        responseGetAllEpic = responseToGETRequest(urlEpic); // Get All Epic
        epics = getListEpic(responseGetAllEpic);
        assertEquals(200, responseGetAllEpic.statusCode(), "Статус ответа (getAllEpics) не 200.");
        assertEquals(1, epics.size(), "Размер списка Epic не верный.");

        responseGetAllSubtask = responseToGETRequest(urlSubtask); // Get All Subtask
        subtasks = getListSubtask(responseGetAllSubtask);
        assertEquals(200, responseGetAllSubtask.statusCode(), "Статус ответа (getAllSubtasks) не 200.");
        assertEquals(1, subtasks.size(), "Размер списка Subtask не верный.");

        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response = responseToGETRequest(url); // Get PrioritizedTasks list is Empty
        assertEquals(200, response.statusCode(), "Статус ответа (Get PrioritizedTasks list) не 200.");
        Type taskListType = new TypeToken<Set<Task>>() {}.getType();
        Set<Task> taskSet = gson.fromJson(response.body(), taskListType);
        assertFalse(taskSet.isEmpty(), "Список PrioritizedTasks не пуст.");
        assertEquals(2, taskSet.size(), "Не верный размер списка.");

        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpResponse<String> responseHistory = responseToGETRequest(uri); // Get History list is Empty
        assertEquals(200, responseHistory.statusCode(), "Статус ответа (Get History list) не 200.");
        List<Task> history = getListTask(response);
        assertEquals(2, history.size(), "Не верный размер списка.");

        TaskManager taskManager = Managers.getDefaultHttpTaskManager();

        assertEquals(2, taskManager.getHistory().size(), "Размер списка History не верный.");
        assertEquals(1, taskManager.getListedOfAllTasks().size(), "Размер списка задач не верный.");
        assertEquals(1, taskManager.getListedOfAllSubtasks().size(), "Размер списка Subtasks не верный.");
        assertEquals(1, taskManager.getListedOfAllEpics().size(), "Размер списка Epics не верный.");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Размер списка Prioritized не верный.");
    }

    @AfterEach
    void afterEach(){
        kvServer.stop();
        httpTaskServer.stop();
    }
}