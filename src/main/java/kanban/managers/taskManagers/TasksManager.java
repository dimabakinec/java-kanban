package kanban.managers.taskManagers;

import kanban.tasks.Epic;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import java.util.List;
import java.util.Map;

public interface TasksManager {

    // получение мапы всех тасков
    Map<Integer, Task> getTasks();

    // получение мапы всех эпиков
    Map<Integer, Epic> getEpics();

    // получение мапы всех сабтасков
    Map<Integer, Subtask> getSubtasks();

    // создание таска
    Task createTask(Task task);

    // создание сабтаска
    Subtask createSubtask(Subtask subtask);

    // создание эпика
    Epic createEpic(Epic epic);

    // удаление эпика
    void removeEpic(int epicID);

    // удаление сабтаска
    void removeSubtask(int id);

    // удаление таска
    void removeTask(int id);

    // удаление всех тасков, эпиков и сабтасков
    void removeAllTasksEpicsSubtasks();

    // обновление таска
    Task update(Task task);

    // обновление сабтаска
    Subtask update(Subtask subtask);

    // обновление эпика
    Epic update(Epic epic);

    // запрос таска
    Task getTask(int id);

    // запрос сабтаска
    Subtask getSubtask(int id);

    // запрос эпика
    Epic getEpic(int id);

    // получение истории
    List<Task> getHistory();

    // получение приоритетного списка тасков
    List<Task> getPrioritizedTasks();

    // печать списка всех тасков
    void printAllTasks();

    // печать списка всех сабтасков
    void printAllSubtasks();

    // печать списка всех эпиков
    void printAllEpics();

    // печать приоритетного списка
    void printPrioritizedTasks();

    // печать истории
    void printHistory();

}
