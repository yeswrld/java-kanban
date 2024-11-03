package service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.InMemoryTaskManager;
import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DisplayName("Проверка InMemoryTaksManager")
class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void taskManagerInit() {
        taskManager = Managers.getDefault();
    }

    void addTask() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task4 = new Task("Задача 4", "Описание задачи 4", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        Task task5 = new Task("Задача 5", "Описание задачи 5", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task6 = new Task("Задача 6", "Описание задачи 6", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task7 = new Task("Задача 7", "Описание задачи 7", Status.NEW, LocalDateTime.now(), Duration.ZERO);
        Task task8 = new Task("Задача 8", "Описание задачи 8", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task9 = new Task("Задача 9", "Описание задачи 9", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task10 = new Task("Задача 10", "Описание задачи 10", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task11 = new Task("Задача 11", "Описание задачи 11", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task12 = new Task("Задача 12", "Описание задачи 12", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task13 = new Task("Задача 13", "Описание задачи 13", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 15);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 2", Status.IN_PROGRESS, 15);
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
    void getTaskByIdAndHistoryRewriting() { //проверяем заполнение истории просмотра
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
        System.out.println("Размер истории просмотра = " + taskManager.getHistory().size());
        Assertions.assertEquals(3, taskManager.getHistory().size(), "История просмотров не " +
                "соответствует ожидаемому");
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
        Assertions.assertEquals(task, taskManager.getTaskId(2));
    }

    @DisplayName("Вывод всего содержимого трекера задач на экран")
    @Test
    void getAllinTaskManager() {
        addTask();
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpicM(epic2);
        System.out.println("Содержимое TaskManager:");
        System.out.println("Задачи:");
        taskManager.getTasks().forEach(task -> task.setStartTime(LocalDateTime.now()));
        taskManager.getTasks().forEach(System.out::println);

        System.out.println("Подзадачи:");
        taskManager.getSubtasks().forEach(subtask -> subtask.setStartTime(LocalDateTime.now()));
        taskManager.getSubtasks().forEach(System.out::println);
        System.out.println("Эпики:");
        taskManager.getEpics().forEach(epic -> epic.setStartTime(LocalDateTime.now()));
        taskManager.getEpics().forEach(System.out::println);
    }

    @Test
    @DisplayName("Проверяем чистку истории после удаления всех задач из таск манагера")
    void historyCleaningAfterDelAllTypes() {
        addTask();
        System.out.println();
        System.out.println("Задачи по типам в таск манагере:");
        System.out.println("Количество эпиков в таск манагере = " + taskManager.getEpics().size());
        System.out.println("Количество сабтасков в таск манагере = " + taskManager.getSubtasks().size());
        System.out.println("Количество задач в таск манагере = " + taskManager.getTasks().size());
        System.out.println();
        taskManager.addEpicM(new Epic("Эпик 2", "Для проверки"));
        taskManager.getTaskId(1);
        taskManager.getTaskId(2);
        taskManager.getSubTaskId(15);
        taskManager.getSubTaskId(16);
        taskManager.getSubTaskId(1);
        taskManager.getEpicId(14);
        taskManager.getTaskId(2);
        taskManager.getTaskId(3);
        taskManager.getEpicId(18);
        System.out.println("История просмотров до удаления, размер которой - " + taskManager.getHistory().size());
        taskManager.deleteEpics();
        taskManager.deleteTasks();
        System.out.println();
        System.out.println("Размер истории после удаления задач всех типов - " + taskManager.getHistory().size());
        System.out.println(taskManager.getEpics().size());
        System.out.println(taskManager.getSubtasks().size());
        System.out.println(taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getHistory().size());

    }

    @Test
    @DisplayName("Проверка работы для 8 спринта")
    void workingCheck() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime.format(formatter));
        Task task777 = new Task("Задача", "Описание", Status.NEW, dateTime, Duration.ofSeconds(15));
        taskManager.addTaskM(task777);
        task777.setStartTime(dateTime);
        task777.setDuration(Duration.ofSeconds(60));
        System.out.println(taskManager.getTasks());
        System.out.println("Время окончания = " + task777.getEndTime().format(formatter));
    }

    @Test
    @DisplayName("Проверка прироритетности задач")
    void priorityTaskTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Epic epic1 = new Epic("Эпик 1", "Описание 1 ");
        Subtask subtask1 = new Subtask("Последняя задача", "Её описание", Status.IN_PROGRESS, 4);
        task1.setStartTime(LocalDateTime.now());
        task2.setStartTime(LocalDateTime.of(2024, 11, 3, 23, 15, 32));
        subtask1.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask1.setDuration(Duration.ofDays(30));
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        taskManager.addEpicM(epic1);
        taskManager.addSubTaskM(subtask1);
        Assertions.assertEquals("Последняя задача", taskManager.getPrioritizedTasks().getLast().getName());

    }

    @Test
    @DisplayName("Проверка наличия эпика у подзадачи и изменения статуса эпика при изменении статусов у сабтасков")
    void subtaskHaveEpicTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Epic epic1 = new Epic("Эпик 1", "Описание 1 ");

        Subtask subtask1 = new Subtask("Подзадача 1", "Её описание", Status.IN_PROGRESS, 4);
        Subtask subtask2 = new Subtask("Подзадача 2", "Её описание", Status.IN_PROGRESS, 4);
        task1.setStartTime(LocalDateTime.now());
        task2.setStartTime(LocalDateTime.of(2024, 11, 3, 23, 15, 32));
        subtask1.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask1.setDuration(Duration.ofDays(15));
        subtask2.setStartTime(LocalDateTime.of(2025, 12, 4, 20, 00, 55));
        subtask2.setDuration(Duration.ofMinutes(15));
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        taskManager.addEpicM(epic1);
        taskManager.addSubTaskM(subtask1);
        taskManager.addSubTaskM(subtask2);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubstask(subtask1);
        taskManager.updateSubstask(subtask2);
        Assertions.assertEquals(2, taskManager.returnSubtasksOnEpicId(4).size(), "Кол-во сабтасков не соответвует");
        Assertions.assertEquals(Status.DONE, epic1.getStatus(), "Статус эпика не поменялся");

    }

    @Test
    @DisplayName("Проверка пересечения подзадач")
        //Задач в таскманагере типа сабьтаск должно быть 1, так как их стартовое время совпадает
    void subtaskintersectionTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE, LocalDateTime.now(), Duration.ZERO);
        Epic epic1 = new Epic("Эпик 1", "Описание 1 ");

        Subtask subtask1 = new Subtask("Подзадача 1", "Её описание", Status.IN_PROGRESS, 4);
        Subtask subtask2 = new Subtask("Подзадача 2", "Её описание", Status.IN_PROGRESS, 4);
        task1.setStartTime(LocalDateTime.now());
        task2.setStartTime(LocalDateTime.of(2024, 11, 3, 23, 15, 32));
        subtask1.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask1.setDuration(Duration.ofDays(15));
        subtask2.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask2.setDuration(Duration.ofMinutes(15));
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        taskManager.addEpicM(epic1);
        taskManager.addSubTaskM(subtask1);
        taskManager.addSubTaskM(subtask2);
        Assertions.assertEquals(1, taskManager.returnSubtasksOnEpicId(4).size(), "Кол-во сабтасков не соответвует");


    }
}