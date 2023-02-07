import kanban.managers.historyManagers.HistoryManager;
import kanban.managers.Managers;
import kanban.tasks.Task;
import kanban.tasks.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class InMemoryHistoryManagerTest {

    private final List<Task> emptyList = new ArrayList<>();
    private HistoryManager manager;

    @BeforeEach
    public void loadInitialConditions() {

        manager = Managers.getDefaultHistoryManager();

    }

    @Test
    public void addTasksToHistoryTest() {

        var task1 = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        var task2 = new Task(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        var task3 = new Task(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());

    }

    @Test
    public void clearHistoryTest() {

        var task1 = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        var task2 = new Task(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        var task3 = new Task(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.clear();

        assertEquals(emptyList, manager.getHistory());

    }

    @Test
    public void removeTask() {

        var task1 = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        var task2 = new Task(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        var task3 = new Task(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.remove(task2.getId());

        assertEquals(List.of(task1, task3), manager.getHistory());

    }

    @Test
    public void noDuplicatesTest() {

        var task1 = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        var task2 = new Task(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        var task3 = new Task(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task2);
        manager.add(task3);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());

    }

    @Test
    public void noTaskRemoveIfIncorrectIDTest() {

        var task1 = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        var task2 = new Task(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        var task3 = new Task(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.remove(42);
        manager.remove(17);
        manager.remove(9);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());

    }

}
