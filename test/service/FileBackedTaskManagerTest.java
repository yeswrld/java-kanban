package service;

import com.tasktracker.app.model.*;
import com.tasktracker.app.service.FileBackedTaskManager;
import com.tasktracker.app.service.Managers;
import com.tasktracker.app.service.TaskManager;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private TaskManager taskManagerLoad;
    private File file;


    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("storage", ".csv");
        taskManager = FileBackedTaskManager.load(Managers.getDefaultHistory(), file);
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        taskManager.addTaskM(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicM(epic1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 4);
        taskManager.addSubTaskM(subtask2);
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
        Task task5 = new Task("Задача 5", "Описание задачи 5", Status.DONE);
        Task task6 = new Task("Задача 6", "Описание задачи 6", Status.DONE);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 2);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 2", Status.IN_PROGRESS, 2);
        fileBackedManager.addEpicM(epic1);
        fileBackedManager.addSubTaskM(subtask2);
        fileBackedManager.addSubTaskM(subtask3);
        fileBackedManager.addTaskM(task5);
        fileBackedManager.addTaskM(task6);
        FileBackedTaskManager loadFromFile = FileBackedTaskManager.load(Managers.getDefaultHistory(), fileData);
        Assertions.assertTrue(loadFromFile.getEpics().equals(fileBackedManager.getEpics()));
        Assertions.assertTrue(loadFromFile.getSubtasks().equals(fileBackedManager.getSubtasks()));
        Assertions.assertTrue(loadFromFile.getTasks().equals(fileBackedManager.getTasks()));


    }

    @DisplayName("Создание и сохранение в файл")
    @Test
    void createAndSave() throws IOException {

        List<String> linesList = new ArrayList<>();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                linesList.add(line + "\n");
            }
        }
        Assertions.assertEquals(linesList.get(0), "id,type,name,status,description,epic,\n");
        Assertions.assertEquals(linesList.get(1), "3,TASK,Задача 1,NEW,Описание задачи 1,\n");
        Assertions.assertEquals(linesList.get(3), "5,SUBTASK,Подзадача 2,IN_PROGRESS,Описание подзадачи 2,4\n");
    }

    @DisplayName("Очистка Менеджера задач и проверка пустоты хранилища")
    @Test
    void cleaningTaskManager() { // после удаления задач всех типов в истории должна остаться только одна строка - оглавление
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        List<String> linesList = new ArrayList<>();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                linesList.add(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(linesList.get(0), "id,type,name,status,description,epic,\n");
        Assertions.assertTrue(linesList.size() == 1);
    }

    @DisplayName("Загрузка из файла")
    @Test
    void loadFromFile() {
        taskManagerLoad = FileBackedTaskManager.load(Managers.getDefaultHistory(), file);
        List<Task> tasks = new ArrayList<>(taskManagerLoad.getTasks());
        List<Epic> epics = new ArrayList<>((Collection) taskManagerLoad.getEpics());
        List<Subtask> subtasks = new ArrayList<>((Collection) taskManagerLoad.getSubtasks());
        Assertions.assertEquals(tasks.get(0).getName(), "Задача 1");
        Assertions.assertEquals(epics.get(0).getType(), Type.EPIC);
        Assertions.assertEquals(subtasks.get(0).getDescription(), "Описание подзадачи 2");

    }

}