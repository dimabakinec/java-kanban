import kanban.managers.Managers;
import kanban.managers.taskManagers.InMemoryTasksManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTasksManagerTest extends TasksManagerTest<InMemoryTasksManager> {

    @BeforeEach
    public void beforeEach() {
        tasksManager = Managers.getDefaultManager();
    }
}
