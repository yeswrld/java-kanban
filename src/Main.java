import com.tasktracker.app.service.TaskManager;
import com.tasktracker.app.model.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task pereezd, duck, electr;
        Subtask boxPacking, catPacking, sayGoodBye;
        Epic gooseGagaga;
        pereezd = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        gooseGagaga = new Epic("Эпик 1", "Описание эпика 1");
        electr = new Task("Задача 3", "Описание задачи 2", Status.DONE);
        boxPacking = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.DONE,  4);
        catPacking = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE,  4);
        sayGoodBye = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.DONE,  4);
        duck = new Task("Задача 2", "Описание задачи 2", Status.DONE);

        taskManager.addTaskM(pereezd); // 1
        taskManager.addTaskM(duck); // 2
        taskManager.addTaskM(electr); // 3
        taskManager.addEpicM(gooseGagaga); // 4
        taskManager.addSubTaskM(boxPacking); // 5
        taskManager.addSubTaskM(catPacking); // 6
        taskManager.addSubTaskM(sayGoodBye); // 7

        System.out.println("2a. Получение списка всех задач.");
        System.out.println("Задачи: " + taskManager.printTask());
        System.out.println("Подзадачи: " + taskManager.printSubtask());
        System.out.println("Эпики: " + taskManager.printEpic());
        System.out.println();

        System.out.println("2c. Получение по идентификатору.");
        //получение задачи по ИД
        System.out.println("Получение задачи по ИД");
        System.out.println(taskManager.getTaskId(3));

        //получение подзадачи по ИД
        System.out.println("Получение подзадачи по ИД");
        System.out.println(taskManager.getSubTaskId(5));

        //получение эпика по ИД
        System.out.println("Получение эпика по ИД");
        System.out.println(taskManager.getEpicId(4));
        System.out.println();

        System.out.println("3a. Получение списка всех подзадач определённого эпика.");
        //получение списка подзадач
        System.out.println(taskManager.returnSubtasksOnEpicId(4));
        System.out.println();


        System.out.println("4ab Управление статусом эпика");
        Epic epicUpd = taskManager.getEpicId(4);
        System.out.println(epicUpd);
        epicUpd.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epicUpd);
        System.out.println(taskManager.getEpicId(4));
        System.out.println(taskManager.returnSubtasksOnEpicId(4));

        System.out.println("Смена статуса подзадачи");
        Subtask subtaskUpd = taskManager.getSubTaskId(5);
        subtaskUpd.setStatus(Status.IN_PROGRESS);
        System.out.println(taskManager.getSubTaskId(5));
        System.out.println(taskManager.getEpicId(4));
        System.out.println(taskManager.returnSubtasksOnEpicId(4));
        subtaskUpd = taskManager.getSubTaskId(6);
        subtaskUpd.setStatus(Status.DONE);
        taskManager.updateSubstask(subtaskUpd);
        System.out.println();
        System.out.println(taskManager.getSubTaskId(5));
        System.out.println(taskManager.getEpicId(4));
        System.out.println(taskManager.returnSubtasksOnEpicId(4));
        System.out.println();

        System.out.println("Смена наименования задачи");
        System.out.println(taskManager.getTaskId(1));
        Task nameUpd = taskManager.getTaskId(1);
        nameUpd.setName("Возвращение в старый дом");
        taskManager.updateTask(nameUpd);
        System.out.println(taskManager.getTaskId(1));
        System.out.println();

        System.out.println("2f. Удаление по идентификатору.");
        taskManager.removeTaskOnId(1);
        taskManager.removeSubTaskOnId(6);
        taskManager.removeEpicOnId(4);
        System.out.println("Задачи: " + taskManager.printTask());
        System.out.println("Подзадачи: " + taskManager.printSubtask());
        System.out.println("Эпики: " + taskManager.printEpic());
        System.out.println();

        System.out.println("2b. Удаление всех задач.");
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        System.out.println(epicUpd.getSubtaskIdList());
        System.out.println("Задачи: " + taskManager.printTask());
        System.out.println("Подзадачи: " + taskManager.printSubtask());
        System.out.println("Эпики: " + taskManager.printEpic());
    }
}
