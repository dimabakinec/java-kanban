package kanban.managers.taskManagers;

import com.google.gson.reflect.TypeToken;
import kanban.clients.KVTaskClient;
import kanban.utils.Formatter;
import com.google.gson.Gson;
import kanban.tasks.Subtask;
import kanban.tasks.Epic;
import kanban.tasks.Task;

import java.util.List;
import java.util.Map;

public class HttpTasksManager extends FileBackedTasksManager {

    protected KVTaskClient client;
    private final Gson gson;

    public HttpTasksManager() {

        gson = Formatter.createGson();
        client = new KVTaskClient();

    }

    public KVTaskClient getClient() {

        return client;

    }

    @Override
    public void save() {

        var prioritizedTasks = gson.toJson(getPrioritizedTasks());
        client.save("tasks", prioritizedTasks);

        var history = gson.toJson(getHistory());
        client.save("tasks/history", history);

        var tasks = gson.toJson(getTasks());
        client.save("tasks/task", tasks);

        var epics = gson.toJson(getEpics());
        client.save("tasks/epic", epics);

        var subtasks = gson.toJson(getSubtasks());
        client.save("tasks/subtask", subtasks);

    }

    public void load() {

        var jsonPrioritizedTasks = getClient().load("tasks");
        var prioritizedTaskType = new TypeToken<List<Task>>(){}.getType();

        List<Task> priorityTasks = gson.fromJson(jsonPrioritizedTasks, prioritizedTaskType);

        getPrioritizedTasks().addAll(priorityTasks);


        var gsonHistory = getClient().load("tasks/history");
        var historyType = new TypeToken<List<Task>>(){}.getType();

        List<Task> history = gson.fromJson(gsonHistory, historyType);

        getHistory().addAll(history);


        var jsonTasks = getClient().load("tasks/task");
        var taskType = new TypeToken<Map<Integer, Task>>(){}.getType();

        Map<Integer, Task> tasks = gson.fromJson(jsonTasks, taskType);

        getTasks().putAll(tasks);


        var jsonEpics = getClient().load("tasks/epic");
        var epicType = new TypeToken<Map<Integer, Epic>>(){}.getType();

        Map<Integer, Epic> epics = gson.fromJson(jsonEpics, epicType);

        getEpics().putAll(epics);


        var jsonSubtasks = getClient().load("tasks/subtask");
        var subtaskType = new TypeToken<Map<Integer, Subtask>>(){}.getType();

        Map<Integer, Subtask> subtasks = gson.fromJson(jsonSubtasks, subtaskType);

        getSubtasks().putAll(subtasks);

    }

}