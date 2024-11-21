package ServerTest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tasktracker.app.model.Adapters.DurationAdapter;
import com.tasktracker.app.model.Adapters.LocalDateTimeAdapter;
import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.server.HttpTaskServer;
import com.tasktracker.app.service.Managers;
import com.tasktracker.app.service.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManagerEpicsTest {
    protected Gson gson = new Gson().newBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);

    @BeforeEach
    public void serverInit() throws IOException, InterruptedException {
        server.serverStart();
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();

    }

    @AfterEach
    public void serverShutdown() {
        server.stopServer();
    }

    @DisplayName("Создание задачи типа Epic")
    @Test
    public void epicCreateTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getEpics().get(0).getDuration());
        Assertions.assertEquals(200, epicResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getEpics().size());
    }

    @DisplayName("Удаление задачи типа Epic")
    @Test
    public void epicDeleteTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(1, taskManager.getEpics().size());
        URI delUri = URI.create("http://localhost:8080/epics/2");
        HttpRequest epicDeleteRequest = HttpRequest.newBuilder().uri(delUri).DELETE().build();
        HttpResponse<String> response = epicClient.send(epicDeleteRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getEpics());

        Assertions.assertEquals(204, response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(0, taskManager.getEpics().size(), "Кол-во эпиков не соотвествует ожидаемому");
    }

    @DisplayName("Удаление всех задач типа Epic")
    @Test
    public void allEpicDeleteTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        Epic epic2 = new Epic("Эпик для обновления", "Описание эпика 1");
        epic2.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic2.setDuration(Duration.ofHours(1));
        String epicUpdJson = gson.toJson(epic2);

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).DELETE().build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(204, epicResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(0, taskManager.getEpics().size());
    }

    @DisplayName("Обновление задачи типа Epic")
    @Test
    public void epicUpdateTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        Epic epic2 = new Epic("Эпик для обновления", "Описание эпика 1");
        epic2.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic2.setDuration(Duration.ofHours(1));
        epic2.setId(2);
        String epicUpdJson = gson.toJson(epic2);

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest epicUpdHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicUpdJson)).build();
        HttpResponse<String> epicUpdResponse = epicClient.send(epicUpdHttpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getEpics().get(0).getDuration());
        Assertions.assertEquals(200, epicResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals("Эпик для обновления", taskManager.getEpics().getLast().getName());
    }

    @DisplayName("Тест BadRequest - 400")
    @Test
    public void sendBadRequestTest() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/epics");
        HttpClient taskHttpClient = HttpClient.newHttpClient();
        String json = "";
        HttpRequest request = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = taskHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Статус код неккоректного запроса не прошёл");
    }

    @DisplayName("Получение всех задач типа Epic")
    @Test
    public void epicsGetTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        Epic epic2 = new Epic("Эпик для обновления", "Описание эпика 1");
        epic2.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic2.setDuration(Duration.ofHours(1));
        String epicUpdJson = gson.toJson(epic2);

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest epicUpdHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicUpdJson)).build();
        HttpResponse<String> epicUpdResponse = epicClient.send(epicUpdHttpRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest epicsGetHttpRequest = HttpRequest.newBuilder().uri(epicUri).GET().build();
        HttpResponse<String> epicGetResponse = epicClient.send(epicsGetHttpRequest, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(epicGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        JsonObject jsonObject1 = jsonElement.getAsJsonArray().get(1).getAsJsonObject();
        Assertions.assertEquals(200, epicResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(jsonObject.get("name").getAsString(), taskManager.getEpics().getFirst().getName(), "Названия не равны");
        Assertions.assertEquals(jsonObject1.get("id").getAsInt(), taskManager.getEpics().getLast().getId(), "ID не равны");
    }

    @DisplayName("Получение задачи типа Epic по ID")
    @Test
    public void epicGetByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(1, taskManager.getEpics().size());

        URI getUri = URI.create("http://localhost:8080/epics/2");
        HttpRequest epicsGetHttpRequest = HttpRequest.newBuilder().uri(getUri).GET().build();
        HttpResponse<String> epicGetResponse = epicClient.send(epicsGetHttpRequest, HttpResponse.BodyHandlers.ofString());

        List<Task> epicsFromTM = new ArrayList<>(taskManager.getEpics());
        JsonElement jsonElement = JsonParser.parseString(epicGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Assertions.assertEquals(200, epicGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(jsonObject.get("id").getAsInt(), taskManager.getEpics().getFirst().getId()
                , "Кол-во эпиков не соотвествует ожидаемому");
    }

    @DisplayName("Получение задачи типа Epic по ID если он не существует")
    @Test
    public void epicGetByUnrealIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(1, taskManager.getEpics().size());

        URI getUri = URI.create("http://localhost:8080/epics/6");
        HttpRequest epicsGetHttpRequest = HttpRequest.newBuilder().uri(getUri).GET().build();
        HttpResponse<String> epicGetResponse = epicClient.send(epicsGetHttpRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, epicGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
    }

    @DisplayName("Получение подзадач Epic'a по ID")
    @Test
    public void getEpicSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());

        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest task1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> task1Response = taskHttpClient.send(task1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> task2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI getUri = URI.create("http://localhost:8080/epics/2/subtasks");
        HttpRequest epicsGetHttpRequest = HttpRequest.newBuilder().uri(getUri).GET().build();
        HttpResponse<String> epicGetResponse = epicClient.send(epicsGetHttpRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, epicGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        JsonElement jsonElement = JsonParser.parseString(epicGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        Assertions.assertEquals(jsonObject.get("name").getAsString(), taskManager.getSubtasks().getFirst().getName());
    }

    @DisplayName("Тест sendErrorRequestTest - 500")
    @Test
    public void sendErrorRequestTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(1, taskManager.getEpics().size());
        URI delUri = URI.create("http://localhost:8080/epics/delete5/");
        HttpRequest epicDeleteRequest = HttpRequest.newBuilder().uri(delUri).GET().build();
        HttpResponse<String> response = epicClient.send(epicDeleteRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(500, response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Кол-во эпиков не соотвествует ожидаемому");
    }
}
