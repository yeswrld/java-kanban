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
    private TaskManager taskManager, taskManagerLoad;
    private File file;


    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("storage", ".csv");
        taskManager = FileBackedTaskManager.load(Managers.getDefaultHistory(), file);
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        taskManager.addTaskM(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicM(epic1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 3);
        taskManager.addSubTaskM(subtask2);
    }

    @AfterEach
    public void afterEach() {
        file.deleteOnExit();
    }

    @DisplayName("Создание и сохранение в файл")
    @Test
    void createAndSave() throws IOException {

        List<String> linesList = new ArrayList<>();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                linesList.add(line + "\n");
            }
        }
        Assertions.assertEquals(linesList.get(0), "id,type,name,status,description,epic,\n");
        Assertions.assertEquals(linesList.get(1), "2,TASK,Задача 1,NEW,Описание задачи 1,\n");
        Assertions.assertEquals(linesList.get(3), "4,SUBTASK,Подзадача 2,IN_PROGRESS,Описание подзадачи 2,3\n");
    }

    @DisplayName("Очистка Менеджера задач и проверка пустоты хранилища")
    @Test
    void cleaningTaskManager() { // после удаления задач всех типов в истории должна остаться только одна строка - оглавление
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        List<String> linesList = new ArrayList<>();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
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