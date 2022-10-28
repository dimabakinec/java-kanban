

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // создание эпик задачи + 2 подзадачи

        Task newTask1 = new Task();
        newTask1.setName("Задача №1");
        newTask1.setDescription("Описание задачи №1");
        newTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.creatTask(newTask1);

        Task newTask2 = new Task();
        newTask2.setName("Задача №2");
        newTask2.setDescription("Описание задачи №2");
        taskManager.creatTask(newTask2);

        Epic newEpic1 = new Epic();
        newEpic1.setName("Эпик №1");
        newEpic1.setDescription("Описание эпика №1");
        taskManager.creatEpic(newEpic1);

        Subtask newSubtask1 = new Subtask();
        newSubtask1.setEpicId(1);
        newSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        newSubtask1.setName("Подзадача №1 эпика №1");
        newSubtask1.setDescription("Описание подзадачи №1 эпика №1");
        taskManager.creatSubtask(newSubtask1);

        Subtask newSubtask2 = new Subtask();
        newSubtask2.setEpicId(1);
        newSubtask2.setName("Подзадача №2 эпика №1");
        newSubtask2.setDescription("Описание подзадачи №2 эпика №1");
        taskManager.creatSubtask(newSubtask2);

        // создание эпик задачи + 1 подзадача

        Epic newEpic2 = new Epic();
        newEpic2.setName("Эпик №2");
        newEpic2.setDescription("Описание эпика №2");
        taskManager.creatEpic(newEpic2);

        Subtask newSubtask = new Subtask();
        newSubtask.setEpicId(2);
        newSubtask.setName("Подзадача №1 эпика №2");
        newSubtask.setDescription("Описание подзадачи №1 эпика №2");
        taskManager.creatSubtask(newSubtask);

        System.out.println(taskManager.listOfAllTasks());
        System.out.println(taskManager.listOfAllEpics());
        System.out.println(taskManager.listOfAllSubtasks());

        //меняем статусы созданных объектов
//
        newTask1.setTaskId(1);
        newTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(newTask1);

        newTask2.setTaskId(2);
        newTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(newTask2);

        newSubtask1.setEpicId(1);
        newSubtask1.setTaskId(1);
        newSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(newSubtask1);

        newSubtask2.setEpicId(1);
        newSubtask2.setTaskId(2);
        newSubtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(newSubtask2);

        newSubtask.setEpicId(2);
        newSubtask.setTaskId(3);
        newSubtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(newSubtask);

        System.out.println(taskManager.listOfAllTasks());
        System.out.println(taskManager.listOfAllEpics());
        System.out.println(taskManager.listOfAllSubtasks());

        //удаляем эпик и задачу по Id

        taskManager.removeByIdEpics(2);
        taskManager.removeByIdTasks(1);

        //удалить задачу и эпик задачу

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();


        //вывести на печать задачи со статусами


    }
}
