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

public class PriorityTasksTest {
    protected Gson gson = new Gson().newBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);

    @BeforeEach
    public void serverInit() throws IOException {
        taskManager.deleteTasks();
        server.serverStart();
    }

    @AfterEach
    public void serverShutdown() {
        server.stopServer();
    }

    @DisplayName("Проверка приоритетности задач")
    @Test
    public void getPriorityTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now().plusMinutes(5));
        epic.setDuration(Duration.ofHours(1));
        HttpClient epicClient = HttpClient.newHttpClient();
        String epicJson = gson.toJson(epic);
        URI epicUri = URI.create("http://localhost:8080/epics");

        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());

        URI taskUri = URI.create("http://localhost:8080/tasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 10);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW, LocalDateTime.now().plusMinutes(15), 11);
        String task1Json = gson.toJson(task1);
        String task2Json2 = gson.toJson(task2);

        HttpRequest task1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpResponse<String> task1Response = taskHttpClient.send(task1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(task2Json2)).build();
        HttpResponse<String> task2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI subtasksUri = URI.create("http://localhost:8080/subtasks");
        HttpClient subtaskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now().minusDays(5), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(75), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(subtasksUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = subtaskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest subtask2HttpRequest = HttpRequest.newBuilder().uri(subtasksUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> subtask2Response = subtaskHttpClient.send(subtask2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI priorityTasks = URI.create("http://localhost:8080/prioritized");
        HttpClient priorityClient = HttpClient.newHttpClient();
        HttpRequest prioritizedRequest = HttpRequest.newBuilder().uri(priorityTasks).GET().build();
        HttpResponse<String> priorityResponse = priorityClient.send(prioritizedRequest, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(priorityResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        Assertions.assertEquals(200, priorityResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(jsonObject.get("name").getAsString(), taskManager.getPrioritizedTasks().get(0).getName());
        Assertions.assertEquals(5, jsonObject.get("id").getAsInt());
        Assertions.assertEquals("Подзадача 1", jsonObject.get("name").getAsString());

        JsonObject jsonObject1 = jsonElement.getAsJsonArray().get(3).getAsJsonObject();
        Assertions.assertEquals("Подзадача 2", jsonObject1.get("name").getAsString());

    }
}
