package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.tasktracker.model.TaskStatus.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;
    Epic newEpic1 = new Epic("Эпик №1", "Описание эпика №1");
    Subtask newSubtask1 = new Subtask("Подзадача №1 эпика №1", "Описание подзадачи №1 эпика №1"
            ,  0, null, 1);
    Subtask newSubtask2 = new Subtask("Подзадача №2 эпика №1", "Описание подзадачи №2 эпика №2", 30
            , LocalDateTime.of(2022, 12, 27, 21, 0),  1);
    Task newTask1 = new Task("Задача №1", "Описание задачи №1", 0, null);
    Task newTask2 = new Task("Задача №2", "Описание задачи №2"
            , 50, LocalDateTime.of(2022, 12, 27, 20, 0));
    Epic newEpic2 = new Epic("Эпик №2", "Описание эпика №2");

    @Test
    void shouldCreateTaskWithTheStatusNew() { // Проверяет, что задача создана и статус NEW.
        taskManager.createTask(newTask1);
        final int idTask = newTask1.getUin();

        final Task savedTask = taskManager.getByIDTask(idTask);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListedOfAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask1, tasks.get(0), "Задачи не совпадают.");

        assertEquals(newTask1.getStatus(), NEW, "Статус задачи не NEW");
    }

    @Test
    void shouldCreateEpicWithTheStatusNewDateNull() { //Эпик создан добавлен в список со статусом NEW и startTime = NULL
        taskManager.createEpic(newEpic1);

        final int idEpic = newEpic1.getUin();
        final Epic savedEpic = taskManager.getByIDEpic(idEpic);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(NEW, savedEpic.getStatus(), "Статус эпика не NEW");
        assertNull(newEpic1.getStartTime(), "Дата старта эпика не null");

        final List<Epic> epics = taskManager.getListedOfAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(newEpic1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateSubTaskWithTheStatusNew() { //Subtask создан добавлен список со статусом NEW и в список эпика.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);

        final int id = newSubtask1.getUin();
        final Subtask savedSubtask = taskManager.getByIDSubtask(id);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(newSubtask1, savedSubtask, "Подзадачи не совпадают.");
        assertEquals(NEW, newSubtask1.getStatus(), "Статус подзадачи не NEW");

        final List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(newSubtask1, subtasks.get(0), "Подзадачи не совпадают.");

        Epic epic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = epic.getListIdSubtasks();

        assertEquals(1, subtasksList.size(), "Неверное количество задач.");
        assertEquals(id, subtasksList.get(0), "ID подзадачи в списке не совпадают.");
    }

    @Test
    void shouldThrowExceptionCreatingSubtaskWithoutEpic() { //Subtask создан без эпика - ошибка!
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.createSubtask(newSubtask1));
        assertEquals("Эпик не создан или не найден!", exception.getMessage());
    }

    @Test
    void getListedOfAllTasks() { // Проверяем список созданных задач.
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);

        final List<Task> tasks = taskManager.getListedOfAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getListedOfAllTasks(), tasks, "Списки не равны!");
    }

    @Test
    void shouldGetEmptyTasksList() { // Проверяем список задач - список должен быть пустым.
        final List<Task> tasks = taskManager.getListedOfAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void getListedOfAllEpics() { // Проверяем список созданных эпиков.
        taskManager.createEpic(newEpic1);
        taskManager.createEpic(newEpic2);

        final List<Epic> epics = taskManager.getListedOfAllEpics();

        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(taskManager.getListedOfAllEpics(), epics, "Списки не равны!");
    }

    @Test
    void shouldGetEmptyEpicsList() { //Проверяем список эпиков - список должен быть пустым.
        final List<Epic> epics = taskManager.getListedOfAllEpics();

        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void getListedOfAllSubtasks() { // Проверяем список созданных подзадач.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        final List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getListedOfAllSubtasks(), subtasks, "Списки не равны!");
    }

    @Test
    void deleteAllTasksShouldGetEmptyList() { //Удаляем созданные задачи - список должен быть пустым.
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);

        List<Task> tasks = taskManager.getListedOfAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getListedOfAllTasks(), tasks, "Списки не равны!");

        taskManager.deleteAllTasks();
        tasks = taskManager.getListedOfAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteAllEpicsShouldGetEmptyListEpicsAndSubtasks() { //Удаляем созданные эпики и subtasks.
        taskManager.createEpic(newEpic2);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        List<Epic> epics = taskManager.getListedOfAllEpics();
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(taskManager.getListedOfAllEpics(), epics, "Списки эпиков не равны!");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(taskManager.getListedOfAllSubtasks(), subtasks, "Списки не равны!");

        taskManager.deleteAllEpics();
        epics = taskManager.getListedOfAllEpics();
        subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(0, epics.size(), "Неверное количество эпиков после удаления.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач после удаления эпиков.");
    }

    @Test
    void deleteAllSubtasksShouldGetEmptyList() { // Удаляем subtasks.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        Epic epic = taskManager.getByIDEpic(newEpic1.getUin());
        List<Integer> subtasksList = epic.getListIdSubtasks();
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(2, subtasksList.size(), "Неверное количество задач в списке эпика.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getListedOfAllSubtasks(), subtasks, "Списки не равны!");

        taskManager.deleteAllSubtasks();
        subtasks = taskManager.getListedOfAllSubtasks();
        subtasksList = epic.getListIdSubtasks();

        assertEquals(0, subtasks.size(), "Неверное количество задач.");
        assertEquals(0, subtasksList.size(), "Неверное количество задач в списке эпика.");
    }

    @Test
    void getByIDTask() { // Получение по идентификатору.
        taskManager.createTask(newTask1);

        final Task saveTask = taskManager.getByIDTask(newTask1.getUin());

        assertEquals(newTask1, saveTask, "Задачи не равны.");
    }

    //Получение по идентификатору, если не верный идентификатор или задача не создана.
    @Test
    void getByIDTaskShouldReturnNull() {
        final Task saveTask = taskManager.getByIDTask(newTask1.getUin());

        assertNull(saveTask, "Значение не равно null.");
    }

    @Test
    void getByIDEpic() { // Получение по идентификатору.
        taskManager.createEpic(newEpic1);

        final Epic saveEpic = taskManager.getByIDEpic(newEpic1.getUin());

        assertEquals(newEpic1, saveEpic, "Эпики не равны!");
    }

    //Получение по идентификатору, если не верный идентификатор или эпик не создан.
    @Test
    void getByIDEpicShouldReturnNull() {
        final Epic saveEpic = taskManager.getByIDEpic(newEpic1.getUin());

        assertNull(saveEpic, "Значение не равно null.");
    }

    @Test
    void getByIDSubtask() { // Получение по идентификатору.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);

        final Subtask saveSubtask = taskManager.getByIDSubtask(newSubtask1.getUin());

        assertEquals(newSubtask1, saveSubtask, "Подзадачи не равны!.");
    }

    //Получение по идентификатору, если не верный идентификатор или подзадача не создана.
    @Test
    void getByIDSubtaskShouldReturnNull() {
        final Subtask saveSubtask = taskManager.getByIDSubtask(newSubtask1.getUin());

        assertNull(saveSubtask, "Значение не равно null.");
    }

    @Test
    void updateTask() { // Обновляем задачу.
        taskManager.createTask(newTask1);

        Task saveTask = taskManager.getByIDTask(newTask1.getUin());

        assertEquals(newTask1, saveTask, "Задачи не совпадают!");

        Task newTask1 = new Task(1, "Задача №1", IN_PROGRESS, "Описание задачи №1"
                , 50, LocalDateTime.of(2022, 12, 27, 20, 0));

        taskManager.updateTask(newTask1);

        saveTask = taskManager.getByIDTask(1);

        assertEquals(newTask1.getStatus(), saveTask.getStatus(), "Задачи не совпадают!");

        final List<Task> taskList = taskManager.getListedOfAllTasks();

        assertEquals(1, taskList.size(), "Не верное количество задач!");
        assertEquals(newTask1, taskList.get(0), "Задачи не совпадают!");
    }

    //Ошибка при обновлении - задача отсутствует в списке задач(т.е. не создана).
    @Test
    void updatingWithInvalidIDTaskShouldThrowException(){
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.updateTask(newTask1));

        assertEquals("Задача не найдена!", exception.getMessage());
    }

    @Test
    void updateEpic() { // Обновляем эпик.
        taskManager.createEpic(newEpic1);

        Epic saveEpic = taskManager.getByIDEpic(newEpic1.getUin());

        assertEquals(newEpic1, saveEpic, "Эпики не совпадают!");

        Epic newEpic1 = new Epic(1, "Эпик №1", "Описание эпика №1 изменено");

        taskManager.updateEpic(newEpic1);

        saveEpic = taskManager.getByIDEpic(1);

        assertEquals(newEpic1, saveEpic, "Эпики не совпадают!");

        final List<Epic> epicList = taskManager.getListedOfAllEpics();

        assertEquals(1, epicList.size(), "Не верное количество эпиков!");
        assertEquals(newEpic1, epicList.get(0), "Эпики не совпадают!");
    }

    //Ошибка при обновлении - эпик отсутствует в списке эпиков(т.е. не создан).
    @Test
    void updatingWithInvalidIDEpicShouldThrowException(){
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.updateEpic(newEpic1));

        assertEquals("Эпик не найден!", exception.getMessage());
    }

    // Обновление Subtask, статус эпика должен обновиться на IN_PROGRESS,
    // так же должны обновиться дата старта и завершения эпика.
    @Test
    void updateSubtaskShouldUpdateStatusStartTimeEndTimeEpic() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);

        Subtask saveSubtask = taskManager.getByIDSubtask(newSubtask1.getUin());

        assertEquals(newSubtask1, saveSubtask, "Подзадачи не совпадают!");
        assertEquals(NEW, saveSubtask.getStatus(), "Статус подзадачи не NEW!");

        LocalDateTime date = LocalDateTime.of(2022, 12, 27, 21, 0);
        Subtask newSubtask = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1 изменено",  50, date, 1);
        taskManager.updateSubtask(newSubtask);
        final List<Subtask> subtaskList = taskManager.getListedOfAllSubtasks();
        saveSubtask = taskManager.getByIDSubtask(2);
        Epic epic = taskManager.getByIDEpic(newEpic1.getUin());

        assertEquals(newSubtask, saveSubtask, "Подзадачи не совпадают!");
        assertEquals(1, subtaskList.size(), "Не верное количество задач!");
        assertEquals(IN_PROGRESS, saveSubtask.getStatus(), "Статус подзадачи не IN_PROGRESS!");
        assertEquals(IN_PROGRESS, epic.getStatus(), "Статус эпика не обновился!");
        assertEquals(date, epic.getStartTime(), "Дата старта эпика не обновлена!");
        assertEquals(newSubtask.getEndTime(), epic.getEndTime(), "Дата завершения эпика не обновлена!");
    }

    //Ошибка при обновлении - subtask отсутствует в списке эпиков(т.е. не создан).
    @Test
    void updatingWithInvalidIDSubtaskShouldThrowException(){
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.updateSubtask(newSubtask1));

        assertEquals("Подзадача не найдена!", exception.getMessage());
    }

    @Test
    void shouldDeleteTaskByID() { // Удаляем Задачу по ID.
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);
        final int idTask = newTask1.getUin();

        List<Task> tasks = taskManager.getListedOfAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask1, tasks.get(0), "Задачи не совпадают.");
        assertEquals(newTask2, tasks.get(1), "Задачи не совпадают.");

        taskManager.deleteTaskByID(idTask);
        tasks = taskManager.getListedOfAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(newTask2, tasks.get(0), "Задачи не совпадают.");
    }

    //Ошибка при удалении - задача отсутствует в списке задач(т.е. не создана).
    @Test
    void shouldThrowExceptionIfDeleteWithInvalidIDTask(){
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.deleteTaskByID(newTask1.getUin()));

        assertEquals("Идентификатор задачи указан не верно!", exception.getMessage());
    }

    @Test
    void shouldDeleteEpicByID() { // Удаляем Задачу по ID.
        taskManager.createEpic(newEpic1);
        final int idEpic = newEpic1.getUin();
        taskManager.createEpic(newEpic2);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        List<Epic> epics = taskManager.getListedOfAllEpics();
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");

        taskManager.deleteEpicByID(idEpic);
        epics = taskManager.getListedOfAllEpics();
        subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(1, epics.size(), "Неверное количество эпиков после удаления.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач после удаления эпиков.");
        assertSame(newEpic2, epics.get(0), "Эпики не совпадают!");
    }

    //Ошибка при удалении - эпик отсутствует в списке эпиков(т.е. не создан).
    @Test
    void shouldThrowExceptionIfDeleteWithInvalidIDEpic(){
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.deleteEpicByID(newEpic1.getUin()));

        assertEquals("Идентификатор эпика указан не верно!", exception.getMessage());
    }

    @Test
    void shouldDeleteSubtaskByID() { // Удаляем Задачу по ID.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        Epic epic = taskManager.getByIDEpic(newEpic1.getUin());
        List<Integer> subtasksList = epic.getListIdSubtasks();
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks();

        assertEquals(2, subtasksList.size(), "Неверное количество задач в списке эпика.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getListedOfAllSubtasks(), subtasks, "Списки не равны!");

        taskManager.deleteSubtaskByID(newSubtask1.getUin());
        subtasks = taskManager.getListedOfAllSubtasks();
        subtasksList = epic.getListIdSubtasks();

        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(1, subtasksList.size(), "Неверное количество задач в списке эпика.");
        assertEquals(newSubtask2.getUin(), subtasksList.get(0), "ИД подзадач не совпадают");
        assertSame(newSubtask2, subtasks.get(0), "Подзадачи не равны!");
    }

    //Ошибка при удалении - subtask отсутствует в списке (т.е. не создан).
    @Test
    void shouldThrowExceptionIfDeleteWithInvalidIDSubtask(){
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.deleteSubtaskByID(newSubtask1.getUin()));

        assertEquals("Идентификатор подзадачи указан не верно!", exception.getMessage());
    }

    @Test
    void shouldGetListAllSubtasksOfEpic() { //Проверяем список подзадач по эпику.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        int epicID = newEpic1.getUin();

        List<Integer> subtasksList = newEpic1.getListIdSubtasks(); // Список ИД subtask по эпику.
        List<Subtask> subtasks = taskManager.getListedOfAllSubtasks(); // Список subtask в списке.
        List<Subtask> allSubtasksOfEpic = taskManager.getListAllSubtasksOfEpic(epicID); // subtask по эпику.

        assertEquals(subtasksList.size(), allSubtasksOfEpic.size(), "Размер списков не соответствует!");
        assertEquals(subtasks, allSubtasksOfEpic, "Subtask не равны!");
    }

    @Test
    void shouldReturnNullWithInvalidIDEpic(){ // Если ИД Эпика указан не верно - вернет null.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        List<Integer> subtasksList = newEpic1.getListIdSubtasks(); // Список ИД subtask по эпику 1
        List<Subtask> allSubtasksOfEpic = taskManager.getListAllSubtasksOfEpic(newEpic2.getUin()); // эпик не создан.

        assertEquals(2, subtasksList.size(), "Размер списка не равен 2.");
        assertNull(allSubtasksOfEpic, "Список не вернул null.");
    }

    @Test
    void shouldGetHistoryAfterAddTask() { // Проверяем список истории просмотренных задач.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createTask(newTask1);
        int idTask = newTask1.getUin();
        int idEpic = newEpic1.getUin();
        int idSubtask = newSubtask1.getUin();

        List<Task> listTask = List.of(taskManager.getByIDTask(idTask), taskManager.getByIDSubtask(idSubtask)
                ,taskManager.getByIDEpic(idEpic));
        List<Task> listHistory = taskManager.getHistory();

        assertEquals(3, listHistory.size(), "Размер списка истории не верный.");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");
    }

    // Проверяем список истории просмотренных задач, что после удаления эпика - удаляется и subtask.
    @Test
    void shouldGetEmptyHistoryAfterDeletingEpic() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);

        int idEpic = newEpic1.getUin();
        int idSubtask = newSubtask1.getUin();

        List<Task> listTask = List.of(taskManager.getByIDSubtask(idSubtask)
                ,taskManager.getByIDEpic(idEpic));
        List<Task> listHistory = taskManager.getHistory();

        assertEquals(2, listHistory.size(), "Размер списка истории не верный.");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");

        taskManager.deleteEpicByID(1);
        listHistory = taskManager.getHistory();

        assertEquals(0, listHistory.size(), "Размер списка истории не верный.");
    }

    // Проверяем список истории просмотренных задач, что после удаления задачи и subtask по ИД - список не пуст.
    @Test
    void shouldGetNotEmptyHistoryAfterDeletingTaskAndSubtask() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createTask(newTask1);

        int idEpic = newEpic1.getUin();
        int idSubtask = newSubtask1.getUin();
        int idTask = newTask1.getUin();

        List<Task> listTask = List.of(taskManager.getByIDTask(idTask), taskManager.getByIDSubtask(idSubtask)
                , taskManager.getByIDEpic(idEpic));
        List<Task> listHistory = taskManager.getHistory();

        assertEquals(3, listHistory.size(), "Размер списка истории не верный.");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");

        taskManager.deleteTaskByID(idTask);
        taskManager.deleteSubtaskByID(idSubtask);

        listHistory = taskManager.getHistory();
        listTask = List.of(newEpic1);

        assertEquals(1, listHistory.size(), "Размер списка истории не верный.");
        assertFalse(listHistory.isEmpty(), "История просмотра не пустая!");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");
    }

    // после удаления всех эпиков из списка просмотра - должны остаться только задачи.
    @Test
    void shouldGetNotEmptyHistoryAfterDeletingAllEpic() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);
        taskManager.createEpic(newEpic2);

        List<Task> listTask = List.of(taskManager.getByIDTask(newTask1.getUin())
                , taskManager.getByIDSubtask(newSubtask2.getUin())
                , taskManager.getByIDSubtask(newSubtask1.getUin())
                , taskManager.getByIDEpic(newEpic1.getUin())
                , taskManager.getByIDTask(newTask2.getUin())
                , taskManager.getByIDEpic(newEpic2.getUin()));
        List<Task> listHistory = taskManager.getHistory();


        assertEquals(6, listHistory.size(), "Размер списка истории не верный.");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");

        taskManager.deleteAllEpics();

        listHistory = taskManager.getHistory();
        listTask = List.of(newTask1, newTask2);

        assertEquals(2, listHistory.size(), "Размер списка истории не верный.");
        assertFalse(listHistory.isEmpty(), "История просмотра не пустая!");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");
    }

    // после удаления всех задач и subtask из списка просмотра - должны остаться только эпики.
    @Test
    void shouldGetEmptyHistoryAfterDeletingAllTaskAndSubtask() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);
        taskManager.createEpic(newEpic2);

        List<Task> listTask = List.of(taskManager.getByIDTask(newTask1.getUin())
                , taskManager.getByIDSubtask(newSubtask2.getUin())
                , taskManager.getByIDSubtask(newSubtask1.getUin())
                , taskManager.getByIDEpic(newEpic1.getUin())
                , taskManager.getByIDTask(newTask2.getUin())
                , taskManager.getByIDEpic(newEpic2.getUin()));
        List<Task> listHistory = taskManager.getHistory();


        assertEquals(6, listHistory.size(), "Размер списка истории не верный.");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");

        taskManager.deleteAllSubtasks();
        taskManager.deleteAllTasks();

        listHistory = taskManager.getHistory();
        listTask = List.of(newEpic1, newEpic2);

        assertEquals(2, listHistory.size(), "Размер списка истории не верный.");
        assertFalse(listHistory.isEmpty(), "История просмотра не пустая!");
        assertIterableEquals(listTask, listHistory, "Списки не равны!");
    }

    // Проверяем, что список задач в порядке приоритета по startTime формируется.
    @Test
    void shouldGetPrioritizedTasksAfterAddTask() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);

        List<Task> listTask = List.of(newSubtask2, newTask1);
        Set<Task> listPrioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> list = new LinkedList<>(listPrioritizedTasks);

        assertEquals(listTask.size(), listPrioritizedTasks.size(), "Размеры не соответствуют.");
        assertEquals(listTask, list, "Списки задач в порядке приоритета не равны!");
    }

    @Test
    void shouldGetEmptyListPrioritizedTasksAfterDeletingAllTasks() { //Проверяем, что после удаления задач список пуст.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);

        List<Task> listTask = List.of(newSubtask2, newTask1);
        Set<Task> listPrioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> list = new LinkedList<>(listPrioritizedTasks);

        assertEquals(listTask.size(), listPrioritizedTasks.size(), "Размеры не соответствуют.");
        assertEquals(listTask, list, "Списки задач в порядке приоритета не равны!");

        taskManager.deleteSubtaskByID(2);
        taskManager.deleteTaskByID(3);
        listPrioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(0, listPrioritizedTasks.size(), "Список задач в порядке приоритета не пуст!");
    }

    //Проверяем, что после удаления одной задачи не удаляются все и сортируются верно.
    @Test
    void shouldGetListPrioritizedTasksAfterDeletingOneTask() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);

        List<Task> listTask = List.of(newTask2, newSubtask2, newTask1);
        Set<Task> listPrioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> list = new LinkedList<>(listPrioritizedTasks);

        assertEquals(listTask.size(), listPrioritizedTasks.size(), "Размеры не соответствуют.");
        assertEquals(listTask, list, "Списки задач в порядке приоритета не равны!");

        taskManager.deleteEpicByID(1);

        listPrioritizedTasks = taskManager.getPrioritizedTasks();
        list = new LinkedList<>(listPrioritizedTasks);
        listTask = List.of(newTask2, newTask1);

        assertEquals(2, listPrioritizedTasks.size(), "Список задач в порядке приоритета не пуст!");
        assertEquals(listTask, list, "Задачи в списке задач в порядке приоритета не верны!");
    }

    // Проверяем, что после обновления задач, задачи не дублируются и сортируются верно.
    @Test
    void shouldGetListPrioritizedTasksAfterUpdateStartDateTask() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);

        List<Task> listTask = List.of(newTask2, newSubtask2, newTask1);
        Set<Task> listPrioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> list = new LinkedList<>(listPrioritizedTasks);

        assertEquals(listTask.size(), listPrioritizedTasks.size(), "Размеры не соответствуют.");
        assertEquals(listTask, list, "Списки задач в порядке приоритета не равны!");

        newTask1 = new Task(3, "Задача №1", IN_PROGRESS, "Описание задачи №1"
                , 30, LocalDateTime.of(2022, 12, 27, 19, 0));
        newTask2 = new Task(4, "Задача №2", IN_PROGRESS, "Описание задачи №2"
                , 50, LocalDateTime.of(2022, 12, 27, 21, 31));
        taskManager.updateTask(newTask1);
        taskManager.updateTask(newTask2);

        listPrioritizedTasks = taskManager.getPrioritizedTasks();
        list = new LinkedList<>(listPrioritizedTasks);
        listTask = List.of(newTask1, newSubtask2, newTask2);

        assertEquals(listTask, list, "Задачи в списке задач в порядке приоритета после обновления не верны!");
    }

    @Test
    void shouldStatusEpicNewIfEmptyListSubtaskOfEpic () { // Статус эпика NEW если список подзадач эпика пуст.
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        taskManager.updateSubtask(newSubtask1);

        final int idEpic = newEpic1.getUin();
        final Epic savedEpic = taskManager.getByIDEpic(idEpic);
        List<Integer> subtasksList = savedEpic.getListIdSubtasks();

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(IN_PROGRESS, savedEpic.getStatus(), "Статус эпика не IN_PROGRESS");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");

        taskManager.deleteAllSubtasks();
        subtasksList = savedEpic.getListIdSubtasks();

        assertEquals(newEpic1.getStatus(), NEW, "Статус эпика не NEW");
        assertEquals(0, subtasksList.size(), "Неверное количество задач.");
    }

    // Статус эпика NEW если все подзадачи в списке со статусом NEW.
    @Test
    void shouldStatusEpicNewIfStatusAllSubtaskOfEpicNEW () {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(NEW, newSubtask1.getStatus(), "Статус подзадачи не NEW");
        assertEquals(NEW, newSubtask2.getStatus(), "Статус подзадачи не NEW");
        assertEquals(NEW, savedEpic.getStatus(), "Статус эпика не NEW");
        assertEquals(list, listString, "Не верно указаны ID");
    }

    // Статус эпика DONE если все подзадачи в списке со статусом DONE.
    @Test
    void shouldStatusEpicDoneIfStatusAllSubtaskOfEpicDone () {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", DONE
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", DONE
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  newEpic1.getUin());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(DONE, newSubtask1.getStatus(), "Статус подзадачи не DONE");
        assertEquals(DONE, newSubtask2.getStatus(), "Статус подзадачи не DONE");
        assertEquals(DONE, savedEpic.getStatus(), "Статус эпика не DONE");
        assertEquals(list, listString, "Не верно указаны ID");
    }
    // Статус эпика IN_PROGRESS если все подзадачи в списке со статусом IN_PROGRESS.
    @Test
    void shouldStatusEpicInProgressIfStatusAllSubtaskOfEpicInProgress () {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", IN_PROGRESS
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  newEpic1.getUin());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(IN_PROGRESS, newSubtask1.getStatus(), "Статус подзадачи не IN_PROGRESS");
        assertEquals(IN_PROGRESS, newSubtask2.getStatus(), "Статус подзадачи не IN_PROGRESS");
        assertEquals(IN_PROGRESS, savedEpic.getStatus(), "Статус эпика не IN_PROGRESS");
        assertEquals(list, listString, "Не верно указаны ID");
    }

    // Статус эпика IN_PROGRESS если все подзадачи в списке со статусом IN_PROGRESS и DONE.
    @Test
    void shouldStatusEpicInProgressIfStatusAllSubtaskOfEpicInProgressAndDone () {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", DONE
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  newEpic1.getUin());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(IN_PROGRESS, newSubtask1.getStatus(), "Статус подзадачи не IN_PROGRESS");
        assertEquals(DONE, newSubtask2.getStatus(), "Статус подзадачи не DONE");
        assertEquals(IN_PROGRESS, savedEpic.getStatus(), "Статус эпика не IN_PROGRESS");
        assertEquals(list, listString, "Не верно указаны ID");
    }

    // Статус эпика IN_PROGRESS если все подзадачи в списке со статусом IN_PROGRESS и NEW.
    @Test
    void shouldStatusEpicInProgressIfStatusAllSubtaskOfEpicInProgressAndNEW () {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", NEW
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  newEpic1.getUin());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(IN_PROGRESS, newSubtask1.getStatus(), "Статус подзадачи не IN_PROGRESS");
        assertEquals(NEW, newSubtask2.getStatus(), "Статус подзадачи не NEW");
        assertEquals(IN_PROGRESS, savedEpic.getStatus(), "Статус эпика не IN_PROGRESS");
        assertEquals(list, listString, "Не верно указаны ID");
    }

    // Статус эпика IN_PROGRESS если все подзадачи в списке со статусом DONE и NEW.
    @Test
    void shouldStatusEpicInProgressIfStatusAllSubtaskOfEpicDoneAndNEW () {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", DONE
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", NEW
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  newEpic1.getUin());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic1, savedEpic, "Задачи не совпадают.");
        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(DONE, newSubtask1.getStatus(), "Статус подзадачи не DONE");
        assertEquals(NEW, newSubtask2.getStatus(), "Статус подзадачи не NEW");
        assertEquals(IN_PROGRESS, savedEpic.getStatus(), "Статус эпика не IN_PROGRESS");
        assertEquals(list, listString, "Не верно указаны ID");
    }

    // Если был статус эпика DONE и обновлена подзадача на IN_PROGRESS, статус эпика - IN_PROGRESS.
    @Test
    void shouldStatusEpicInProgressAfterUpdateStatusSubtaskFromDoneToNew() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", DONE
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", DONE
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 21, 0),  newEpic1.getUin());

        taskManager.updateSubtask(newSubtask1);
        taskManager.updateSubtask(newSubtask2);

        final Epic savedEpic = taskManager.getByIDEpic(newEpic1.getUin());
        final List<Integer> subtasksList = savedEpic.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(DONE, newSubtask1.getStatus(), "Статус подзадачи не DONE");
        assertEquals(DONE, newSubtask2.getStatus(), "Статус подзадачи не DONE");
        assertEquals(DONE, savedEpic.getStatus(), "Статус эпика не DONE");
        assertEquals(list, listString, "Не верно указаны ID");

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", NEW
                ,"Описание подзадачи №1 эпика №1",  0, null, newEpic1.getUin());
        taskManager.updateSubtask(newSubtask1);

        assertEquals(NEW, newSubtask1.getStatus(), "Статус подзадачи не NEW");
        assertEquals(IN_PROGRESS, savedEpic.getStatus(), "Статус эпика не IN_PROGRESS");
    }

    /*проверяем обновление дат эпика:
    Время начала — дата старта самой ранней подзадачи, а время завершения — время окончания самой поздней из задач.*/
    @Test
    void shouldUpdateEpicStartTimeEndEndTimeAfterUpdateSubtasks() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1",  40
                , LocalDateTime.of(2022, 12, 27, 21, 0), newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", IN_PROGRESS
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 20, 0),  newEpic1.getUin());
        taskManager.updateSubtask(newSubtask2);
        taskManager.updateSubtask(newSubtask1);

        final List<Integer> subtasksList = newEpic1.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(list, listString, "Не верно указаны ID");
        assertEquals(newSubtask2.getStartTime(), newEpic1.getStartTime(), "StartTime эпика не верное!");
        assertEquals(newSubtask1.getEndTime(), newEpic1.getEndTime(), "EndTime эпика не верное!");
        assertEquals(Duration.ofMinutes(70), newEpic1.getDuration(), "Период выполнения эпика не верный!");
    }

    /*проверяем обновление дат эпика:
    Время начала и время завершения должны быть null после удаления всех subtasks.*/
    @Test
    void shouldUpdateEpicStartTimeEndEndTimeAfterDeleteAllSubtasks() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        newSubtask1 = new Subtask(2, "Подзадача №1 эпика №1", IN_PROGRESS
                ,"Описание подзадачи №1 эпика №1",  40
                , LocalDateTime.of(2022, 12, 27, 21, 0), newEpic1.getUin());
        newSubtask2 = new Subtask(3, "Подзадача №2 эпика №1", IN_PROGRESS
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 20, 0),  newEpic1.getUin());
        taskManager.updateSubtask(newSubtask2);
        taskManager.updateSubtask(newSubtask1);

        final List<Integer> subtasksList = newEpic1.getListIdSubtasks();
        String list = newSubtask1.getUin() + ", " + newSubtask2.getUin();
        String listString = subtasksList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));

        assertEquals(2, subtasksList.size(), "Неверное количество задач.");
        assertEquals(list, listString, "Не верно указаны ID");
        assertEquals(newSubtask2.getStartTime(), newEpic1.getStartTime(), "StartTime эпика не верное!");
        assertEquals(newSubtask1.getEndTime(), newEpic1.getEndTime(), "EndTime эпика не верное!");
        assertEquals(Duration.ofMinutes(70), newEpic1.getDuration(), "Период выполнения эпика не верный!");

        taskManager.deleteAllSubtasks();

        assertNull(newEpic1.getStartTime(), "StartTime эпика не верное!");
        assertNull(newEpic1.getEndTime(), "EndTime эпика не верное!");
        assertEquals(Duration.ofMinutes(0), newEpic1.getDuration(), "Период выполнения эпика не верный!");
    }

    //Должно быть исключение при создании задачи с одинаковым временем.
    @Test
    void shouldThrowExceptionWhenAddTaskWithTheSameTime() {
        taskManager.createTask(newTask2);
        Task newTask = new Task(2, "Задача №2", NEW, "Описание задачи №2"
                , 50, LocalDateTime.of(2022, 12, 27, 20, 0));
        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.createTask(newTask));
        assertEquals("Обнаружено пересечение планируемой даты старта задачи. " +
                "Задача не может быть обновлена или добавлена.", exception.getMessage());
    }

    //Должно быть исключение при создании подзадачи с пересекающимся временем.
    @Test
    void shouldThrowExceptionWhenAddSubtaskWithDateIntersection() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);

        Subtask newSubtask = new Subtask(3, "Подзадача №2 эпика №1", NEW
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 20, 50),  newEpic1.getUin());

        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.createTask(newSubtask));
        assertEquals("Обнаружено пересечение планируемой даты старта задачи. " +
                "Задача не может быть обновлена или добавлена.", exception.getMessage());
    }

    //Должно быть исключение при обновлении подзадачи с пересекающимся временем.
    @Test
    void shouldThrowExceptionWhenUpdateSubtaskWithDateIntersection() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask2);

        Subtask newSubtask = new Subtask(2, "Подзадача №2 эпика №1", NEW
                , "Описание подзадачи №2 эпика №2", 30
                , LocalDateTime.of(2022, 12, 27, 20, 30),  newEpic1.getUin());

        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.updateSubtask(newSubtask));
        assertEquals("Обнаружено пересечение планируемой даты старта задачи. " +
                "Задача не может быть обновлена или добавлена.", exception.getMessage());
    }

    //Должно быть исключение при обновлении задачи с пересекающимся временем.
    @Test
    void shouldThrowExceptionWhenUpdateTaskWithDateIntersection() {
        taskManager.createEpic(newEpic1);
        taskManager.createSubtask(newSubtask2);
        taskManager.createTask(newTask2);

        Task newTask = new Task(3, "Задача №2", NEW, "Описание задачи №2"
                , 50, LocalDateTime.of(2022, 12, 27, 20, 30));

        final ManagerException exception = assertThrows(
                ManagerException.class,
                () -> taskManager.updateTask(newTask));
        assertEquals("Обнаружено пересечение планируемой даты старта задачи. " +
                "Задача не может быть обновлена или добавлена.", exception.getMessage());
    }
}