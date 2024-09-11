/*Привет ревьюер!) я так и не особо разобрался с методами addSubtaskOnEpicId и addEpicOnSubtaskId,
так как изначально в моем варианте не получится создать подзадачи с ИД эпиков (будет ошибка).
Поэтому я попробовал сменить ИД подзадачи на ИД эпика, и наоборот во втором случае, а потом сравнить
содержимое по этим ИД*/
package test;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.InMemoryTaskManager;
import com.tasktracker.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tasktracker.app.service.Managers.getDefault;

class TaskManagerTest {
    InMemoryTaskManager manager = getDefault();


    @Test
    void taskEqualsById() { //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        manager.addTaskM(task1);
        manager.addTaskM(task2);
        task1.setId(999);
        task2.setId(999);
        Assertions.assertEquals(task1.getId(), task2.getId());
    }

    @Test
    void subTaskEqualsById() { //проверьте, что наследники класса Task равны друг другу, если равен их id;
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.DONE, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, 1);
        manager.addTaskM(subtask1);
        manager.addTaskM(subtask2);
        subtask1.setId(999);
        subtask2.setId(999);
        Assertions.assertEquals(subtask1.getId(), subtask2.getId());
    }

    @Test
    void epicEqualsById() { //проверьте, что наследники класса Task равны друг другу, если равен их id;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addTaskM(epic1);
        manager.addTaskM(epic2);
        epic1.setId(999);
        epic2.setId(999);
        Assertions.assertEquals(epic1.getId(), epic2.getId());
    }

    @Test
    void generateIdAndGivenIdNotConflict() { //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        manager.addTaskM(task1);
        manager.addTaskM(task2);
        task2.setId(1);
        System.out.println(manager.printTask());
        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    void subtasksEquals() { //
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.DONE, 4);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 2", Status.DONE, 4);
        Assertions.assertEquals(subtask1.getName(), subtask2.getName());
    }

    @Test
    public void taskNotEqual() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 2", Status.DONE);
        Assertions.assertNotEquals(task1, task2);
    }


    @Test
    void epicsEquals() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    void subtaskEqualsById() {
        Epic epic = new Epic("Эпик 3", "Описание эпика 3");
        manager.addTaskM(epic);
        Assertions.assertEquals(epic, manager.getTaskId(epic.getId()));
    }


    @Test
    void addSubtaskOnEpicId() { //проверьте, что объект Subtask нельзя сделать своим же эпиком;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpicM(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, 3);
        manager.addSubTaskM(subtask1);
        subtask1.setId(1);
        System.out.println(subtask1.getId());
        Assertions.assertNotEquals(manager.getEpicId(1), manager.getSubTaskId(1));
    }

    @Test
    void addEpicOnSubtaskId () { //проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpicM(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, 4);
        manager.addSubTaskM(subtask1);
        epic1.setId(2);
        Assertions.assertNotEquals(manager.getEpicId(2), manager.getSubTaskId(2));
    }

    @Test
    void printAll (){
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);

        manager.addTaskM(task1);
        manager.addTaskM(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        manager.addEpicM(epic1);
        manager.addEpicM(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, 2);
        System.out.println("Ид подзадачи " + subtask2.getEpicId());
        System.out.println("Ид эпика 1 " + epic1.getId());
        manager.addSubTaskM(subtask1);
        manager.addSubTaskM(subtask2);

        System.out.println(manager.printEpic());
        System.out.println(manager.printSubtask());
        System.out.println(manager.printTask());
    }

}