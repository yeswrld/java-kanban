package service;

import com.tasktracker.app.model.*;
import com.tasktracker.app.service.FileBackedTaskManager;
import com.tasktracker.app.service.Managers;
import com.tasktracker.app.service.TaskManager;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@DisplayName("Проверка FileBackedTaskManagerTest")
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private TaskManager taskManagerLoad;
    private File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("storage", ".csv");
        taskManager = FileBackedTaskManager.load(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        file.deleteOnExit();
    }


    @DisplayName("Проверка содержимого менеджеров до и после выгрузки")
    @Test
    void savedFileEqualsFileManager() {
        File fileData = new File("data.csv");
        FileBackedTaskManager fileBackedManager = new FileBackedTaskManager(Managers.getDefaultHistory(), fileData);
        Task task5 = new Task("Задача 5", "Описание задачи 5", Status.IN_PROGRESS);
        Task task6 = new Task("Задача 6", "Описание задачи 6", Status.DONE);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), null);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, 2, LocalDateTime.now(), null);
        fileBackedManager.addEpicM(epic1);
        subtask1.setDuration(Duration.ofHours(1));
        subtask1.setStartTime(LocalDateTime.of(2025, 01, 01, 11, 00));
        subtask2.setDuration(Duration.ofHours(3));
        subtask2.setStartTime(LocalDateTime.of(2025, 01, 01, 12, 03));
        task5.setDuration(Duration.ofHours(1));
        fileBackedManager.addSubTaskM(subtask1);
        fileBackedManager.addSubTaskM(subtask2);
        task5.setStartTime(LocalDateTime.of(2024, 11, 13, 15, 01));
        task6.setStartTime(LocalDateTime.of(2024, 11, 13, 15, 11));
        task5.setDuration(Duration.ofMinutes(10));
        task6.setDuration(Duration.ofMinutes(13));
        System.out.println();
        fileBackedManager.addTaskM(task5);
        fileBackedManager.addTaskM(task6);
        fileBackedManager.getPrioritizedTasks().forEach(System.out::println);
        FileBackedTaskManager loadFromFile = FileBackedTaskManager.load(Managers.getDefaultHistory(), fileData);
        Assertions.assertTrue(loadFromFile.getTasks().equals(fileBackedManager.getTasks()));
        Assertions.assertTrue(loadFromFile.getEpics().equals(fileBackedManager.getEpics()));
        Assertions.assertTrue(loadFromFile.getSubtasks().equals(fileBackedManager.getSubtasks()));
        Assertions.assertTrue(loadFromFile.getTasks().equals(fileBackedManager.getTasks()));


    }

    @DisplayName("Создание и сохранение в файл")
    @Test
    void createAndSave() throws IOException {
        createTask();
        List<String> linesList = new ArrayList<>();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                linesList.add(line + "\n");
            }
        }
        Assertions.assertEquals(linesList.get(0), "id,type,name,status,description,epic, startTime, duration\n");
        Assertions.assertEquals(linesList.get(1), "2,TASK,Задача 1,NEW,Описание задачи 1,,2025-01-01T12:15:30,PT1M\n");
        Assertions.assertEquals(linesList.get(3), "4,SUBTASK,Подзадача 2,IN_PROGRESS,Описание подзадачи 2,3,2025-11-04T20:00:55,PT360H\n");
    }

    @DisplayName("Очистка Менеджера задач и проверка пустоты хранилища")
    @Test
    void cleaningTaskManager() { // после удаления задач всех типов в истории должна остаться только одна строка - оглавление
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();
        List<String> linesList = new ArrayList<>();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                linesList.add(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(linesList.get(0), "id,type,name,status,description,epic, startTime, duration\n");
        Assertions.assertTrue(linesList.size() == 1);
    }

    @DisplayName("Загрузка из файла")
    @Test
    void loadFromFile() {
        createTask();
        taskManagerLoad = FileBackedTaskManager.load(Managers.getDefaultHistory(), file);
        List<Task> tasks = new ArrayList<>(taskManagerLoad.getTasks());
        List<Epic> epics = new ArrayList<>((Collection) taskManagerLoad.getEpics());
        List<Subtask> subtasks = new ArrayList<>((Collection) taskManagerLoad.getSubtasks());
        Assertions.assertEquals(tasks.get(0).getName(), "Задача 1");
        Assertions.assertEquals(epics.get(0).getType(), Type.EPIC);
        Assertions.assertEquals(subtasks.get(0).getDescription(), "Описание подзадачи 2");

    }


}