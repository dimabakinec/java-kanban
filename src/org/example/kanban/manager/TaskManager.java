package org.example.kanban.manager;

import org.example.kanban.task.*;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int idGenerator = 0;

    /**
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
                Epic epic = epics.get(subtask.getEpicId());
                epic.addListIdSubtasks(idGenerator); // Добавили id в список подзадач эпика
                if (epic.getStatus() == TaskStatus.DONE) { // Если стаус эпика завершен, меняем на в работе
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            } else {
                System.out.println("Эпик не создан или не найден!"); // Если эпик не найден
            }
        }
    }

    // Получение списка всех задач.

    public ArrayList<Task> getListOfAllTasks() { // Получение списка всех задач.
        ArrayList<Task> taskArrayList = new ArrayList<>(); // Создаем список задач
        taskArrayList.addAll(tasks.values()); // Добавим в список все значения HashMap tasks
        return new ArrayList<>(tasks.values());// Возвращаем список
    }

    public ArrayList<Epic> getListOfAllEpics() { // Получение списка всех задач.
        ArrayList<Epic> epicsArrayList = new ArrayList<>(); // Создаем список задач
        epicsArrayList.addAll(epics.values()); // Добавим в список все значения HashMap tasks
        return new ArrayList<>(epics.values());// Возвращаем список

    }

    public ArrayList<Subtask> getListOfAllSubtasks() { // Получение списка всех задач.
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>(); // Создаем список задач
        subtasksArrayList.addAll(subtasks.values()); // Добавим в список все значения HashMap tasks
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
            epic.removeListIdSubtask(id);
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

    public ArrayList<Subtask> getListOfAllSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Subtask> listOfAllSubbtasks = new ArrayList<>();
            for (int idSubtask : epic.getSubtaskIds()) {
                listOfAllSubbtasks.add(subtasks.get(idSubtask));
            }
            return listOfAllSubbtasks;
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
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Подзадача не найдена!");
        }
    }



    //метод на изменения статуса при изменении org.example.kanban.task.Subtask'a

    public void updateStatusEpic(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            for (int idSubtask : epic.getSubtaskIds()) { // Проверяем статусы задач
                TaskStatus statusSubtask = subtasks.get(idSubtask).getStatus();
                switch (statusSubtask) {
                    case NEW:
                        newStatus++;
                        break;
                    case DONE:
                        doneStatus++;
                        break;
                }
            }
            if (newStatus == epic.getSubtaskIds().size()) {
                epic.setStatus(TaskStatus.NEW);
            } else if (doneStatus == epic.getSubtaskIds().size()) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
