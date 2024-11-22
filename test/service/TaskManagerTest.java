package service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager;
    Task task1;
    Epic epic1;
    Subtask subtask1;


    void createTask() {
        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 1);
        task1.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 15, 30));
        task1.setDuration(Duration.ofMinutes(1));
        taskManager.addTaskM(task1);
        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicM(epic1);
        subtask1 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 3);
        subtask1.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask1.setDuration(Duration.ofDays(15));
        taskManager.addSubTaskM(subtask1);
        epic1.setStartTime(subtask1.getStartTime());
    }

    @Test
    void taskEqualTaskInManager() {
        createTask();
        Assertions.assertEquals(task1.getId(), taskManager.getTasks().get(0).getId());
        Assertions.assertEquals(subtask1.getName(), taskManager.getSubtasks().getFirst().getName());
        Assertions.assertEquals(epic1.getStatus(), taskManager.getEpics().getFirst().getStatus());
    }


    @DisplayName("Проверяем заполняемость трекера задач")
    @Test
    void taskManagerFilling() { //проверка добавления задач
        createTask();
        Assertions.assertNotNull(taskManager, "Трекер задач пустой");
    }


    @Test
    @DisplayName("Проверяем заполняемость истории")
    void getTaskByIdAndHistoryRewriting() { //проверяем заполнение истории просмотра
        createTask();
        System.out.println();
        System.out.println("Количество эпиков в таск манагере = " + taskManager.getEpics().size());
        System.out.println("Количество сабтасков в таск манагере = " + taskManager.getSubtasks().size());
        System.out.println("Количество задач в таск манагере = " + taskManager.getTasks().size());
        System.out.println();
        System.out.println("Полная история просмотров, с порядком следования задач при записи и без дубликатов");
        taskManager.getTaskId(taskManager.getTasks().getFirst().getId());
        System.out.println("Размер истории просмотра = " + taskManager.getHistory().size());
        Assertions.assertEquals(1, taskManager.getHistory().size(), "История просмотров не " +
                "соответствует ожидаемому");
    }

    @DisplayName("Проверяем равенство задач если равен их ИД")
    @Test
    void taskEqualsById() { //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        task1.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 15, 30));
        task1.setDuration(Duration.ofMinutes(1));
        task2.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 20, 30));
        task2.setDuration(Duration.ofMinutes(1));
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
        subtask1.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask1.setDuration(Duration.ofDays(15));
        subtask2.setStartTime(LocalDateTime.of(2025, 12, 4, 20, 00, 55));
        subtask2.setDuration(Duration.ofMinutes(15));
        taskManager.addTaskM(subtask1);
        taskManager.addTaskM(subtask2);
        subtask1.setId(999);
        subtask2.setId(999);
        Assertions.assertEquals(subtask1.getId(), subtask2.getId());
    }


    @DisplayName("Проверяем что задачи с одинаковым ИД не конфликтуют")
    @Test
    void generateIdAndGivenIdNotConflict() { //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        task1.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 15, 30));
        task1.setDuration(Duration.ofMinutes(1));
        task2.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 20, 30));
        task2.setDuration(Duration.ofMinutes(1));
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        task2.setId(1);
        Assertions.assertNotEquals(task1, task2);
    }

    @DisplayName("Подзадачи не равны по описанию")
    @Test
    void subtasksNotEqualsByDescription() { //
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.DONE, 2, LocalDateTime.now(), 0);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 2", Status.DONE, 2, LocalDateTime.now(), 0);
        subtask1.setDuration(Duration.ofHours(1));
        subtask1.setStartTime(LocalDateTime.of(2025, 01, 01, 11, 00));
        subtask2.setDuration(Duration.ofHours(3));
        subtask2.setStartTime(LocalDateTime.of(2025, 01, 01, 12, 03));
        taskManager.addEpicM(epic);
        taskManager.addSubTaskM(subtask1);
        taskManager.addSubTaskM(subtask2);
        Assertions.assertNotEquals(subtask1.getDescription(), subtask2.getDescription());
    }

    @DisplayName("Проверяем что поля задачи не меняются после добавления")
    @Test
    void taskNotChangeOnAdd() { //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер;
        createTask();
        System.out.println(task1.getName());
        Assertions.assertEquals(task1.getName(), taskManager.getTaskId(2).getName());
    }

    @DisplayName("Вывод всего содержимого трекера задач на экран")
    @Test
    void getAllinTaskManager() {
        createTask();
        System.out.println("Содержимое TaskManager:");
        System.out.println("Задачи:");
        taskManager.getTasks().forEach(task -> task.setStartTime(LocalDateTime.now()));
        taskManager.getTasks().forEach(System.out::println);
        System.out.println("Эпики:");
        taskManager.getEpics().forEach(System.out::println);
        System.out.println("Подзадачи:");
        taskManager.getSubtasks().forEach(System.out::println);

    }

    @Test
    @DisplayName("Проверяем чистку истории после удаления всех задач из таск манагера")
    void historyCleaningAfterDelAllTypes() {
        createTask();
        System.out.println();
        System.out.println("Задачи по типам в таск манагере:");
        System.out.println("Количество эпиков в таск манагере = " + taskManager.getEpics().size());
        System.out.println("Количество сабтасков в таск манагере = " + taskManager.getSubtasks().size());
        System.out.println("Количество задач в таск манагере = " + taskManager.getTasks().size());
        System.out.println();
        taskManager.addEpicM(new Epic("Эпик 2", "Для проверки"));
        taskManager.getTaskId(2);
        System.out.println("История просмотров до удаления, размер которой - " + taskManager.getHistory().size());
        taskManager.deleteEpics();
        taskManager.deleteTasks();
        System.out.println();
        System.out.println("Размер истории после удаления задач всех типов - " + taskManager.getHistory().size()
                + "\n" + "Задачи по типам в таск манагере после удаления: ");
        System.out.println("Количество эпиков в таск манагере = " + taskManager.getEpics().size());
        System.out.println("Количество сабтасков в таск манагере = " + taskManager.getSubtasks().size());
        System.out.println("Количество задач в таск манагере = " + taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getHistory().size());

    }

    @Test
    @DisplayName("Проверка работы для 8 спринта")
    void workingCheck() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime.format(formatter));
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 0);
        task1.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 15, 30));
        task1.setDuration(Duration.ofMinutes(1));
        taskManager.addTaskM(task1);
        task1.setStartTime(dateTime);
        task1.setDuration(Duration.ofSeconds(60));
        System.out.println(taskManager.getTasks());
        System.out.println("Время окончания = " + task1.getEndTime().format(formatter));
    }

    @Test
    @DisplayName("Проверка прироритетности задач")
    void priorityTaskTest() {
        createTask();
        Assertions.assertEquals("Подзадача 2", taskManager.getPrioritizedTasks().getLast().getName());

    }

    @Test
    @DisplayName("Проверка наличия эпика у подзадачи и изменения статуса эпика при изменении статусов у сабтасков")
    void subtaskHaveEpicTest() {
        createTask();
        subtask1.setStatus(Status.DONE);
        Assertions.assertEquals(1, taskManager.returnSubtasksOnEpicId(3).size(), "Кол-во сабтасков не соответвует");
        Assertions.assertEquals(epic1.getStatus(), taskManager.getEpics().getFirst().getStatus());

    }

    @Test
    @DisplayName("Проверка пересечения подзадач")
        //Задач в таскманагере типа сабьтаск должно быть 1, так как их стартовое время совпадает
    void subtaskIntersectionTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        Epic epic1 = new Epic("Эпик 1", "Описание 1 ");
        Subtask subtask1 = new Subtask("Подзадача 1", "Её описание", Status.IN_PROGRESS, 4);
        Subtask subtask2 = new Subtask("Подзадача 2", "Её описание", Status.IN_PROGRESS, 4);

        task1.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 15, 30));
        task1.setDuration(Duration.ofMinutes(1));
        task2.setStartTime(LocalDateTime.of(2025, 01, 1, 12, 20, 30));
        task2.setDuration(Duration.ofMinutes(1));
        subtask1.setStartTime(LocalDateTime.of(2025, 11, 4, 20, 00, 55));
        subtask1.setDuration(Duration.ofDays(15));
        subtask2.setStartTime(LocalDateTime.of(2026, 11, 4, 20, 00, 58));
        subtask2.setDuration(Duration.ofMinutes(15));
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        taskManager.addEpicM(epic1);
        taskManager.addSubTaskM(subtask1);
        taskManager.addSubTaskM(subtask2);
        taskManager.getPrioritizedTasks().forEach(System.out::println);
        Assertions.assertEquals(2, taskManager.returnSubtasksOnEpicId(4).size(), "Кол-во сабтасков не соответвует");


    }


}
