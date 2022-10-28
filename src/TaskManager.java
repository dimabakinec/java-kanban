import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int idNumTask = 0;
    int idNumEpic = 0;
    int idNumSubtask = 0;

    // tasks creation
    public void creatTask(Task task) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.

        if (task != null) {
            idNumTask++;
            task.status = (TaskStatus.NEW); // при создании статус всегда new
            task.taskId = idNumTask;
            tasks.put(idNumTask, task); // добавили задачу в мапу
        }
    }

    // epic creation

    public void creatEpic(Epic epic) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (epic != null) {
            idNumEpic++;
            epic.status = (TaskStatus.NEW); // при создании статус всегда new
            epic.taskId = idNumEpic;
            epics.put(idNumEpic, epic); // добавили задачу в мапу
        }
    }

// subtask creation

    public void creatSubtask(Subtask subtask) { //Функция создания задачи. Сам объект должен передаваться в качестве параметра.
        if (subtask != null) {
            idNumSubtask++;
            subtask.status = (TaskStatus.NEW); // при создании статус всегда new
            subtask.taskId = idNumSubtask;
            subtasks.put(idNumSubtask, subtask); // добавили задачу в мапу
            Epic epic = epics.get(subtask.epicId);
            epic.subtaskIds.add(idNumSubtask); // добавили id в список подзадач эпика
            if (epic.status == TaskStatus.DONE) { // если стаус эпика завершен, меняем на в работе
                epic.status = TaskStatus.IN_PROGRESS;
            }

        }
    }

    // Получение списка всех задач.

    public ArrayList<Task> listOfAllTasks() { // Получение списка всех задач.
        ArrayList<Task> taskArrayList = new ArrayList<>(); // Создаем список задач
        taskArrayList.addAll(tasks.values()); // Добавим в список все значения HashMap tasks
        return taskArrayList; // Возвращаем список
    }

    public ArrayList<Epic> listOfAllEpics() { // Получение списка всех задач.
        ArrayList<Epic> epicsArrayList = new ArrayList<>(); // Создаем список задач
        epicsArrayList.addAll(epics.values()); // Добавим в список все значения HashMap tasks
        return epicsArrayList; // Возвращаем список
    }

    public ArrayList<Subtask> listOfAllSubtasks() { // Получение списка всех задач.
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>(); // Создаем список задач
        subtasksArrayList.addAll(subtasks.values()); // Добавим в список все значения HashMap tasks
        return subtasksArrayList; // Возвращаем список
    }


    //Удаление по идентификатору.

    public void removeByIdTasks(int id) {
        tasks.remove(id);
    }

    public void removeByIdEpics(int id) {
        epics.remove(id);
    }

// Удаление всех задач.

    public void removeAllTasks() { // Получение списка всех задач.
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Получение по идентификатору.

    public Task showTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Идентификатор задачи указан не верно!");
            return null;
        }
    }

    public Epic showEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Идентификатор задачи указан не верно!");
            return null;
        }
    }


    //Получение списка всех подзадач определённого эпика.

    public ArrayList<Subtask> showSubtaskByEpicIds(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> subtaskListOfEpic = new ArrayList<>();
        for (int idSubtask : epic.subtaskIds) {
            subtaskListOfEpic.add(subtasks.get(idSubtask));
        }
        return subtaskListOfEpic;
    }

    // изменение статуса задач
    // Обновление. Новая версия объекта
    // с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        tasks.put(task.taskId, task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.epicId, subtask);
    }


}
