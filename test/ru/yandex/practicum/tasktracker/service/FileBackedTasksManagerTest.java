package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefaultNewManager();
    }

    //Восстановление с пустым списком задач.
    @Test
    void shouldRestoreFromFileWithoutErrorWithEmptyListTasks(){
        final List<Task> tasks = taskManager.getListedOfAllTasks();
        final List<Epic> epics = taskManager.getListedOfAllEpics();
        final List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();
        final List<Task> history = taskManager.getHistory();

        assertTrue(tasks.isEmpty(), "Список задач не пустой!");

        assertTrue(epics.isEmpty(), "Список эпиков не пустой!");

        assertTrue(subtasks.isEmpty(), "Список подзадач не пустой!");

        assertTrue(history.isEmpty(), "Список истории не пустой!");
    }

    //Восстановление с пустым списком истории.
    @Test
    void shouldRestoreTasksFromFileWithoutErrorWithEmptyHistoryList(){
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);
        taskManager.createEpic(newEpic2);

        taskManager = Managers.getDefaultNewManager();

        final List<Task> tasks = taskManager.getListedOfAllTasks();
        final List<Epic> epics = taskManager.getListedOfAllEpics();
        final List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();
        final List<Task> history = taskManager.getHistory();

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
        taskManager.createEpic(newEpic1);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);
        taskManager.createEpic(newEpic2);

        taskManager = Managers.getDefaultNewManager();

        final List<Task> tasks = taskManager.getListedOfAllTasks();
        final List<Epic> epics = taskManager.getListedOfAllEpics();
        final List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();
        final List<Task> history = taskManager.getHistory();

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
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);
        taskManager.createEpic(newEpic2);

        taskManager = Managers.getDefaultNewManager();

        final List<Task> tasks = taskManager.getListedOfAllTasks();
        final List<Epic> epics = taskManager.getListedOfAllEpics();
        final List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();
        Set<Task> listPrioritizedTasks = taskManager.getPrioritizedTasks();

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
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createTask(newTask1);
        final int idTask = newTask1.getUin();
        final int idEpic = newEpic1.getUin();
        final int idSubtask = newSubtask1.getUin();

        List<Task> tasks = taskManager.getListedOfAllTasks();
        List<Epic> epics = taskManager.getListedOfAllEpics();
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertTrue(tasks.contains(taskManager.getByIDTask(idTask)), "Задачи не совпадают.");
        assertTrue(epics.contains(taskManager.getByIDEpic(idEpic)), "Задачи не совпадают.");
        assertTrue(subtasks.contains(taskManager.getByIDSubtask(idSubtask)), "Задачи не совпадают.");

        List<Task> history = taskManager.getHistory();
        List<Task> list = List.of(newTask1, newEpic1, newSubtask1);

        assertEquals(3, history.size(), "Задачи не совпадают.");
        assertEquals(list, history, "Задачи не совпадают.");

        taskManager = Managers.getDefaultNewManager();

        tasks = taskManager.getListedOfAllTasks();
        epics = taskManager.getListedOfAllEpics();
        subtasks = taskManager.getListedOfAllSubtasks();
        history = taskManager.getHistory();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(3, history.size(), "Задачи не совпадают.");
    }

    // Сохранение и восстановление состояния - Эпик с подзадачами
    @Test
    void shouldSaveAndRestoreTasksFromFileWithoutErrorEpicWithSubtasks(){
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        List<Integer> listSubtasksOfEpic = newEpic1.getListIdSubtasks();
        List<Epic> epics = taskManager.getListedOfAllEpics();
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = listSubtasksOfEpic.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertEquals(2, listSubtasksOfEpic.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(list, listString, "Значения не верны!");

        taskManager = Managers.getDefaultNewManager();

        epics = taskManager.getListedOfAllEpics();
        subtasks = taskManager.getListedOfAllSubtasks();
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
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);

        assertEquals(2, newSubtask1.getUin(), "Значения не верны!");

        taskManager = Managers.getDefaultNewManager();
        taskManager.createSubtask(newSubtask2);

        assertEquals(3, newSubtask2.getUin(), "Значения не верны!");
    }

    @AfterEach
    void afterEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
    }
}