package org.example.kanban.manager;

import org.example.kanban.model.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idGenerator = 0;


    /**
     * метод создает новую задачу
     *
     * @param task
     */
    public void createTask(Task task) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (task != null) {
            idGenerator++;
            task.setStatus((TaskStatus.NEW)); // при создании статус всегда new
            task.setId(idGenerator);
            tasks.put(idGenerator, task); // добавили задачу в мапу
        }
    }

    /**
     * @param epic
     */
    public void createEpic(Epic epic) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (epic != null) {
            idGenerator++;
            epic.setStatus((TaskStatus.NEW)); // при создании статус всегда new
            epic.setId(idGenerator);
            epics.put(idGenerator, epic); // добавили задачу в мапу
        }
    }

    /**
     * @param subtask
     */
    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            if (epics.containsKey(subtask.getEpicId())) { // Проверяем наличие эпика
                idGenerator++;
                subtask.setStatus(TaskStatus.NEW); // При создании статус всегда new
                subtask.setId(idGenerator);
                subtasks.put(idGenerator, subtask); // Добавили подзадачу в мапу
                Epic epic = epics.get(subtasks.get(idGenerator).getEpicId());
                epic.addSubtaskId(idGenerator);
                //добавить id подзадачи в эпик и после уже вызвать метод
                // по обновлению статуса эпика updateStatusEpic
                updateStatusEpic(epic);
                epicStatus(subtask);
            }
        }
    }

    public void epicStatus(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(idGenerator);
        if (epic.getStatus() == TaskStatus.DONE) { // Если стаус эпика завершен, меняем на в работе
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            System.out.println("Эпик не создан или не найден!");
        }
    }

    // Получение списка всех задач.
    public ArrayList<Task> getAllTasks() { // Получение списка всех задач.
        return new ArrayList<>(tasks.values());// Возвращаем список
    }

    public ArrayList<Epic> getAllEpics() { // Получение списка всех задач.
        return new ArrayList<>(epics.values());// Возвращаем список
    }

    public ArrayList<Subtask> getAllSubtasks() { // Получение списка всех задач.
        return new ArrayList<>(subtasks.values()); // Возвращаем список
    }

    //Удаление по идентификатору.
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
        } else {
            System.out.println("Идентификатор таска указан не верно!");
        }
    }

    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int idSubtask : epic.getSubtaskIds()) { // удаляем все подзадачи данного эпика
                subtasks.remove(idSubtask);
            }
            epics.remove(id); // Удаляем Эпик
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
        }
    }

    public void removeSubtaskByID(Integer id) { // Удаление по идентификатору подзадачи.
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.removeSubtaskId(id);
            subtasks.remove(id); // Удаляем подзадачу
            updateStatusEpic(epic); // Обновляем статус Эпика
        }
    }

    // Удаление всех задач.
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    //Получение по идентификатору.
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Идентификатор задачи указан не верно!");
            return null;
        }
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
            return null;
        }
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            System.out.println("Идентификатор подзадачи указан не верно!");
            return null;
        }
    }

    //Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> getSubtasksFromEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Subtask> listOfAllSubtasks = new ArrayList<>();
            for (int idSubtask : epic.getSubtaskIds()) {
                listOfAllSubtasks.add(subtasks.get(idSubtask));
            }
            return listOfAllSubtasks;
        } else {
            System.out.println("Идентификатор эпика указан не верно!");
            return null;
        }
    }

    // изменение статуса задач
    // Обновление. Новая версия объекта
    // с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена!");
        }
    }
    public void updateEpic(Epic epic) { // Обновление. Новая версия объекта с верным id передаётся в виде параметра.
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не найден!");
        }
    }
    public void updateSubtask(Subtask subtask) { // Обновление.
        if (subtasks.containsKey(subtask.getId()) &&
                epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    //метод на изменения статуса при изменении org.example.kanban.task.Subtask'a
    public void updateStatusEpic(Epic epic) {
        boolean isDone = true;
        boolean isNew = true;
        List<Subtask> subTaskList = getSubtasksFromEpic(epic.getId());
        for (Subtask subTask : subTaskList) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                isDone = false;
                isNew = false;
                break;
            } else if (subTask.getStatus() == TaskStatus.DONE) {
                isNew = false;
                if (!isDone) {
                    break;
                }
            } else if (subTask.getStatus() == TaskStatus.NEW) {
                isDone = false;
                if (!isNew) {
                    break;
                }
            }
        }
        if (epic.getSubtaskIds().size() == 0 || isNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

}
