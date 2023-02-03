import kanban.managers.Managers;
import kanban.managers.taskManagers.FileBackedTasksManager;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TasksManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void beforeEach() {
        tasksManager = Managers.getDefaultManager();
    }

    //Восстановление с пустым списком задач.
    @Test
    void shouldRestoreFromFileWithoutErrorWithEmptyListTasks(){
        final List<Task> tasks = tasksManager.getAllTasks();
        final List<Epic> epics = tasksManager.getAllEpics();
        final List<Subtask> subtasks = tasksManager.getAllSubtasks();
        final List<Task> history = tasksManager.getHistory();

        assertTrue(tasks.isEmpty(), "Список задач не пустой!");

        assertTrue(epics.isEmpty(), "Список эпиков не пустой!");

        assertTrue(subtasks.isEmpty(), "Список подзадач не пустой!");

        assertTrue(history.isEmpty(), "Список истории не пустой!");
    }

    //Восстановление с пустым списком истории.
    @Test
    void shouldRestoreTasksFromFileWithoutErrorWithEmptyHistoryList(){
        tasksManager.createEpic(newEpic1);
        tasksManager.createSubtask(newSubtask1);
        tasksManager.createSubtask(newSubtask2);
        tasksManager.createTask(newTask1);
        tasksManager.createTask(newTask2);
        tasksManager.createEpic(newEpic2);

        tasksManager = Managers.getDefaultManager();

        final List<Task> tasks = tasksManager.getAllTasks();
        final List<Epic> epics = tasksManager.getAllEpics();
        final List<Subtask> subtasks = tasksManager.getAllSubtasks();
        final List<Task> history = tasksManager.getHistory();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");

        assertTrue(history.isEmpty(), "Список истории не пустой!");
    }

    // Сохранение и восстановление состояния - Эпик без подзадач
    @Test
    void shouldSaveAndRestoreTasksFromFileWithoutErrorEpicWithoutSubtasks(){
        tasksManager.createEpic(newEpic1);
        tasksManager.createTask(newTask1);
        tasksManager.createTask(newTask2);
        tasksManager.createEpic(newEpic2);

        tasksManager = Managers.getDefaultManager();

        final List<Task> tasks = tasksManager.getAllTasks();
        final List<Epic> epics = tasksManager.getAllEpics();
        final List<Subtask> subtasks = tasksManager.getAllSubtasks();
        final List<Task> history = tasksManager.getHistory();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        assertTrue(subtasks.isEmpty(), "Список не пустой!");

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");

        assertTrue(history.isEmpty(), "Список истории не пустой!");
    }

    //Восстановление с не пустым списком задач.
    @Test
    void shouldRestoreFromFileWithoutErrorWithNotEmptyListTasks(){
        tasksManager.createEpic(newEpic1);
        tasksManager.createSubtask(newSubtask1);
        tasksManager.createSubtask(newSubtask2);
        tasksManager.createTask(newTask1);
        tasksManager.createTask(newTask2);
        tasksManager.createEpic(newEpic2);

        tasksManager = Managers.getDefaultManager();

        final List<Task> tasks = tasksManager.getAllTasks();
        final List<Epic> epics = tasksManager.getAllEpics();
        final List<Subtask> subtasks = tasksManager.getAllSubtasks();
        Set<Task> listPrioritizedTasks = tasksManager.getPrioritizedTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");

        assertEquals(4, listPrioritizedTasks.size(), "Неверное количество задач.");
    }

    //Восстановление с непустым списком истории.
    @Test
    void shouldRestoreTasksFromFileWithoutErrorWithNotEmptyHistoryList(){
        tasksManager.createEpic(newEpic1);
        tasksManager.createSubtask(newSubtask1);
        tasksManager.createTask(newTask1);
        final int idTask = newTask1.getUin();
        final int idEpic = newEpic1.getUin();
        final int idSubtask = newSubtask1.getUin();

        List<Task> tasks = tasksManager.getAllTasks();
        List<Epic> epics = tasksManager.getAllEpics();
        List<Subtask> subtasks = tasksManager.getAllSubtasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertTrue(tasks.contains(tasksManager.getTaskById(idTask)), "Задачи не совпадают.");
        assertTrue(epics.contains(tasksManager.getEpicById(idEpic)), "Задачи не совпадают.");
        assertTrue(subtasks.contains(tasksManager.getSubtaskById(idSubtask)), "Задачи не совпадают.");

        List<Task> history = tasksManager.getHistory();
        List<Task> list = List.of(newTask1, newEpic1, newSubtask1);

        assertEquals(3, history.size(), "Задачи не совпадают.");
        assertEquals(list, history, "Задачи не совпадают.");

        tasksManager = Managers.getDefaultManager();

        tasks = tasksManager.getAllTasks();
        epics = tasksManager.getAllEpics();
        subtasks = tasksManager.getAllSubtasks();
        history = tasksManager.getHistory();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(3, history.size(), "Задачи не совпадают.");
    }

    // Сохранение и восстановление состояния - Эпик с подзадачами
    @Test
    void shouldSaveAndRestoreTasksFromFileWithoutErrorEpicWithSubtasks(){
        tasksManager.createEpic(newEpic1);
        tasksManager.createSubtask(newSubtask1);
        tasksManager.createSubtask(newSubtask2);

        List<Integer> listSubtasksOfEpic = newEpic1.getListIdSubtasks();
        List<Epic> epics = tasksManager.getAllEpics();
        List<Subtask> subtasks = tasksManager.getAllSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = listSubtasksOfEpic.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertEquals(2, listSubtasksOfEpic.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(list, listString, "Значения не верны!");

        tasksManager = Managers.getDefaultManager();

        epics = tasksManager.getAllEpics();
        subtasks = tasksManager.getAllSubtasks();
        listSubtasksOfEpic = newEpic1.getListIdSubtasks();
        listString = listSubtasksOfEpic.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertEquals(2, listSubtasksOfEpic.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals("2, 3", listString, "Значения не верны!");
    }

    //Проверяем генерацию ИД после восстановления
    @Test
    void shouldGenerateID3AfterRestoreFile() {
        tasksManager.createEpic(newEpic1);
        tasksManager.createSubtask(newSubtask1);

        assertEquals(2, newSubtask1.getUin(), "Значения не верны!");

        tasksManager = Managers.getDefaultManager();
        tasksManager.createSubtask(newSubtask2);

        assertEquals(3, newSubtask2.getUin(), "Значения не верны!");
    }

    @AfterEach
    void afterEach() {
        tasksManager.removeAllTasks();
        tasksManager.removeAllEpics();
    }
}
