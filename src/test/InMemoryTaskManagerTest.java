package test;

import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.InMemoryTaskManager;
import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void addTask() {
        taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 2", Status.DONE);
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
    }

    @Test
    void taskManagerNotWork() {
        Assertions.assertNotNull(taskManager, "Не работает");
    }

    @Test
    void getTaskById(){ //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
        System.out.println(taskManager.getTaskId(1));
        System.out.println(taskManager.getTaskId(2));
        Assertions.assertNotNull(taskManager.getTaskId());
    }

}