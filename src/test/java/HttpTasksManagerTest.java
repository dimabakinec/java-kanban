
import kanban.managers.taskManagers.HttpTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import kanban.tasks.enums.TaskType;
import org.junit.jupiter.api.Test;
import kanban.servers.KVServer;
import kanban.tasks.Subtask;
import kanban.tasks.Epic;
import kanban.tasks.Task;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTasksManagerTest extends TasksManagerTest<HttpTasksManager> {

    protected KVServer server;

    @BeforeEach
    public void loadInitialConditions() throws IOException {

        server = new KVServer();
        server.start();

        manager = new HttpTasksManager();

    }

    @AfterEach
    void serverStop() {

        server.stop();

    }

    @Test
    void loadFromServerTest() {

        var task1 = manager.createTask(
                new Task("Task1", "Task1D", Instant.ofEpochSecond(5000), 5));

        var epic1 = manager.createEpic(
                new Epic("Epic1", "Epic1D", TaskType.EPIC));

        var subtask1 = manager.createSubtask(
                new Subtask("Subtask1", "Subtask1D", Instant.EPOCH, 50, epic1.getId()));

        var subtask2 = manager.createSubtask(
                new Subtask("Subtask2", "Subtask2D", Instant.ofEpochSecond(11111), 50, epic1.getId()));

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());

        manager.load();
        var tasks = manager.getTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        var history = manager.getHistory();

        assertNotNull(history);
        assertEquals(4, history.size());

    }

}