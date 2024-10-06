package service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.HistoryManager;
import com.tasktracker.app.service.InMemoryHistoryManager;
import com.tasktracker.app.service.InMemoryTaskManager;
import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка InMemoryTaksManager")
class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void taskManagerInit() {
        taskManager = Managers.getDefault();
    }

    void addTask() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE);
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        Task task4 = new Task("Задача 4", "Описание задачи 4", Status.NEW);
        Task task5 = new Task("Задача 5", "Описание задачи 5", Status.DONE);
        Task task6 = new Task("Задача 6", "Описание задачи 6", Status.DONE);
        Task task7 = new Task("Задача 7", "Описание задачи 7", Status.NEW);
        Task task8 = new Task("Задача 8", "Описание задачи 8", Status.DONE);
        Task task9 = new Task("Задача 9", "Описание задачи 9", Status.DONE);
        Task task10 = new Task("Задача 10", "Описание задачи 10", Status.DONE);
        Task task11 = new Task("Задача 11", "Описание задачи 11", Status.DONE);
        Task task12 = new Task("Задача 12", "Описание задачи 12", Status.DONE);
        Task task13 = new Task("Задача 13", "Описание задачи 13", Status.DONE);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 14);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 2", Status.IN_PROGRESS, 14);
        Subtask subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 2", Status.IN_PROGRESS, 14);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        taskManager.addTaskM(task3);
        taskManager.addTaskM(task4);
        taskManager.addTaskM(task5);
        taskManager.addTaskM(task6);
        taskManager.addTaskM(task7);
        taskManager.addTaskM(task8);
        taskManager.addTaskM(task9);
        taskManager.addTaskM(task10);
        taskManager.addTaskM(task11);
        taskManager.addTaskM(task12);
        taskManager.addTaskM(task13);
        taskManager.addEpicM(epic1);
        taskManager.addSubTaskM(subtask2);
        taskManager.addSubTaskM(subtask3);
        taskManager.addSubTaskM(subtask4);
    }

    @DisplayName("Проверяем заполняемость трекера задач")
    @Test
    void taskManagerFilling() { //проверка добавления задач
        addTask();
        Assertions.assertNotNull(taskManager, "Трекер задач пустой");
    }


    @Test
    @DisplayName("Проверяем заполняемость истории")
    void getTaskByIdAndHistoryRewriting() { //проверяем работу с удалением по ИД эпиков, тасков, или эпиков с его сабтасками
        addTask();
        System.out.println();
        System.out.println("Количество эпиков в таск манагере = " + taskManager.getEpics().size());
        System.out.println("Количество сабтасков в таск манагере = " + taskManager.getSubtasks().size());
        System.out.println("Количество задач в таск манагере = " + taskManager.getTasks().size());
        System.out.println();
        System.out.println("Полная история просмотров, с порядком следования задач при записи и без дубликатов");
        taskManager.getTaskId(1);
        taskManager.getTaskId(2);
        taskManager.getSubTaskId(15);
        taskManager.getSubTaskId(16);
        taskManager.getSubTaskId(1);
        taskManager.getEpicId(14);
        taskManager.getTaskId(2);
        taskManager.getTaskId(3);
        System.out.println("Размерность истории просомотра = " + taskManager.getHistory().size());
        for (int i = 0; i < taskManager.getHistory().size(); i++) {
            System.out.println(taskManager.getHistory().get(i));
        }
        System.out.println();
        System.out.println("Размерность истории просмотров после удаления Таска, и Сабтаска");
        taskManager.removeTaskOnId(1);
        taskManager.removeSubTaskOnId(15);
        System.out.println("Размерность истории просомотра = " + taskManager.getHistory().size());
        for (int i = 0; i < taskManager.getHistory().size(); i++) {
            System.out.println(taskManager.getHistory().get(i));
        }
        System.out.println();
        System.out.println("Размерность истории просмотров после удаления эпика");
        taskManager.removeEpicOnId(14);
        System.out.println("Размерность истории просомотра = " + taskManager.getHistory().size());
        for (int i = 0; i < taskManager.getHistory().size(); i++) {
            System.out.println(taskManager.getHistory().get(i));
        }
        System.out.println();
        System.out.println("Размерность истории просмотров после удаления последних записей");
        taskManager.removeTaskOnId(2);
        taskManager.removeTaskOnId(3);
        System.out.println("Размерность истории просомотра = " + taskManager.getHistory().size());
        for (int i = 0; i < taskManager.getHistory().size(); i++) {
            System.out.println(taskManager.getHistory().get(i));
        }
        System.out.println();
        System.out.println("Количество эпиков в таск манагере = " + taskManager.getEpics().size());
        System.out.println("Количество сабтасков в таск манагере = " + taskManager.getSubtasks().size());
        System.out.println("Количество задач в таск манагере = " + taskManager.getTasks().size());
    }

    @DisplayName("Проверяем равенство задач если равен их ИД")
    @Test
    void taskEqualsById() { //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        task1.setId(999);
        task2.setId(999);
        Assertions.assertEquals(task1.getId(), task2.getId());
    }

    @DisplayName("Проверяем что задачи равны друг другу, если равен их ИД")
    @Test
    void subTaskEqualsById() { //проверьте, что наследники класса Task равны друг другу, если равен их id;
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.DONE, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, 1);
        taskManager.addTaskM(subtask1);
        taskManager.addTaskM(subtask2);
        subtask1.setId(999);
        subtask2.setId(999);
        Assertions.assertEquals(subtask1.getId(), subtask2.getId());
    }

    @DisplayName("Проверяем что эпики равны друг другу если равен их ИД")
    @Test
    void epicEqualsById() { //проверьте, что наследники класса Epic равны друг другу, если равен их id;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addTaskM(epic1);
        taskManager.addTaskM(epic2);
        epic1.setId(999);
        epic2.setId(999);
        Assertions.assertEquals(epic1.getId(), epic2.getId());
    }

    @DisplayName("Проверяем что задачи с одинаковым ИД не конфликтуют")
    @Test
    void generateIdAndGivenIdNotConflict() { //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        task2.setId(1);
        Assertions.assertNotEquals(task1, task2);
    }

    @DisplayName("Подзадачи не равны по описанию")
    @Test
    void subtasksNotEqualsByDescription() { //
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.DONE, 1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 2", Status.DONE, 1);
        taskManager.addEpicM(epic);
        taskManager.addSubTaskM(subtask1);
        taskManager.addSubTaskM(subtask2);
        Assertions.assertNotEquals(subtask1.getDescription(), subtask2.getDescription());
    }

    @DisplayName("Проверяем что поля задачи не меняются после добавления")
    @Test
    void taskNotChangeOnAdd() { //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер;
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        taskManager.addTaskM(task);
        Assertions.assertEquals(task, taskManager.getTaskId(1));
    }

    @DisplayName("Вывод всего содержимого трекера задач на экран")
    @Test
    void getAllinTaskManager() {
        addTask();
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpicM(epic2);
        System.out.println("Содержимое TaskManager:");
        System.out.println("Задачи:");
        for (int i = 0; i < taskManager.getTasks().size(); i++) {
            System.out.println(taskManager.getTasks().get(i));
        }
        System.out.println("Эпики:");
        for (int i = 0; i < taskManager.getEpics().size(); i++) {
            System.out.println(taskManager.getEpics().get(i));
        }
        System.out.println("Подзадачи:");
        for (int i = 0; i < taskManager.getSubtasks().size(); i++) {
            System.out.println(taskManager.getSubtasks().get(i));
        }
    }
}