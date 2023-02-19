package ru.yandex.practicum.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Subtask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;
    Task newTask1 = new Task(1, "Задача №1", TaskStatus.NEW, "Описание задачи №1"
            , 0, null);
    Task newTask2 = new Task(2, "Задача №2", TaskStatus.NEW, "Описание задачи №2"
            , 0, LocalDateTime.of(2022, 12, 27, 20, 0));
    Epic newEpic1 = new Epic(3, "Эпик №1", "Описание эпика №1");
    Subtask newSubtask1 = new Subtask(4, "Подзадача №1 эпика №1", TaskStatus.NEW
            ,"Описание подзадачи №1 эпика №1",  0, null, 3);

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskHistoryManagerShouldReturnListOneTask() { //Добавляет задачу в историю просмотра.
        historyManager.add(newTask1);
        final List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty(), "История пустая.");
        assertEquals(1, history.size(), "Не верный размер списка!");
    }

    @Test
    void addTwoTaskHistoryManagerShouldReturnListTwoTask() { //Добавляет задачу в историю просмотра.
        historyManager.add(newTask1);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty(), "История пустая.");
        assertEquals(1, history.size(), "Не верный размер списка!");

        historyManager.add(newTask2);
        history = historyManager.getHistory();
        List<Task> list = List.of(newTask1, newTask2);

        assertEquals(2, history.size(), "Не верный размер списка!");
        assertEquals(list, history, "Списки не равны!");
    }

    //Если Task равен null (в случае ошибочной передачи пустой задачи), то запись не должна быть добавлена в список.
    @Test
    void ShouldReturnEmptyListHistoryIfAddTheTaskIsNull() {
        historyManager.add(null);
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "Не верный размер списка!");
        assertTrue(history.isEmpty(), "История не пустая!");
    }

    //Удаляем 1 задачу (начало) в списке.
    @Test
    void removeTaskFromTheBeginningOfTheHistoryList() {
        historyManager.add(newTask1);
        historyManager.add(newTask2);
        historyManager.add(newSubtask1);
        List<Task> history = historyManager.getHistory();
        List<Task> list = List.of(newTask1, newTask2, newSubtask1);

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");

        historyManager.remove(newTask1.getUin());
        history = historyManager.getHistory();
        list = List.of(newTask2, newSubtask1);

        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");
    }

    //Удаляем 2 задачу (середина) в списке.
    @Test
    void removeTaskFromTheMiddleOfTheHistoryList() {
        historyManager.add(newTask1);
        historyManager.add(newTask2);
        historyManager.add(newSubtask1);
        List<Task> history = historyManager.getHistory();
        List<Task> list = List.of(newTask1, newTask2, newSubtask1);

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");

        historyManager.remove(newTask2.getUin());
        history = historyManager.getHistory();
        list = List.of(newTask1, newSubtask1);

        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");
    }

    //Удаляем 3 задачу (конец) в списке.
    @Test
    void removeTaskFromTheLastOfTheHistoryList() {
        historyManager.add(newTask1);
        historyManager.add(newTask2);
        historyManager.add(newSubtask1);
        List<Task> history = historyManager.getHistory();
        List<Task> list = List.of(newTask1, newTask2, newSubtask1);

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");

        historyManager.remove(newSubtask1.getUin());
        history = historyManager.getHistory();
        list = List.of(newTask1, newTask2);

        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");
    }

    @Test
    void emptyList() { //Пустая история просмотров.
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не пустая.");
        assertEquals(0, history.size(), "Не верный размер списка!");
    }

    //Добавляем одну и ту же задачу - в списке дубли не создаются.
    @Test
    void addOneTaskToTheListTwiceWithoutDuplication() {
        historyManager.add(newTask1);
        historyManager.add(newEpic1);
        historyManager.add(newTask2);
        historyManager.add(newTask1);
        final List<Task> history = historyManager.getHistory();
        List<Task> list = List.of(newEpic1, newTask2, newTask1);

        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(list, history, "Списки не равны!");
    }
}