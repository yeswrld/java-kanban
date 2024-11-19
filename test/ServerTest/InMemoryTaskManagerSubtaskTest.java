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

public class InMemoryTaskManagerSubtaskTest {
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

        Epic epic = new Epic("Эпик для тасков", "Описание эпика 1");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofMinutes(22));
        String epicJson = gson.toJson(epic);
        HttpClient epicClient = HttpClient.newHttpClient();
        URI epicUri = URI.create("http://localhost:8080/epics");
        HttpRequest epicHttpRequest = HttpRequest.newBuilder().uri(epicUri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        epicClient.send(epicHttpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void serverShutdown() {
        server.stopServer();
    }

    @DisplayName("Добавление задач типа Subtask")
    @Test
    public void createSubtask() throws IOException, InterruptedException {
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

        Assertions.assertEquals(200, task1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(2, taskManager.getSubtasks().size(), "Кол-во задач не соотвествует ожидаемому");
        Assertions.assertEquals("Подзадача 2", taskManager.getSubtasks().get(1).getName(), "Название задачи не соотвествует ожидаемому");

        System.out.println();
        for (Task task : taskManager.getSubtasks()) {
            System.out.println(task);
        }
    }

    @DisplayName("Удаление задач типа Subtask")
    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
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

        URI delUri = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest subtask1DeleteRequest = HttpRequest.newBuilder().uri(delUri).DELETE().build();
        HttpResponse<String> response = taskHttpClient.send(subtask1DeleteRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasks());

        Assertions.assertEquals(204, response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Кол-во задач не соотвествует ожидаемому");
        Assertions.assertEquals(Duration.ofMinutes(11), taskManager.getSubtasks().getLast().getDuration(), "Длительность задачи не соотвествует ожидаемому");
    }

    @DisplayName("Обновление задач типа Subtask")
    @Test
    public void updateSubtaskByID() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Обновленная подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        subtask2.setId(3);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = taskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> task2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(200, subtask1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Кол-во задач не соотвествует ожидаемому");
        Assertions.assertEquals("Обновленная подзадача 2", taskManager.getSubtasks().getFirst().getName(), "Название задачи не соотвествует ожидаемому");

        System.out.println();
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

    }

    @DisplayName("Тест BadRequest - 400")
    @Test
    public void sendBadRequestTest() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();
        String json = "";
        HttpRequest request = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = taskHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Статус код неккоректного запроса не прошёл");

    }

    @DisplayName("Получение всех задач типа Subtask")
    @Test
    public void getCreatedTasks() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = taskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> subtask2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskUri).GET().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        JsonElement jsonElement = JsonParser.parseString(taskGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        JsonObject jsonObject1 = jsonElement.getAsJsonArray().get(1).getAsJsonObject();
        Assertions.assertEquals(200, subtask1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(taskManager.getSubtasks().get(0).getName(), jsonObject.get("name").getAsString(), "Имена сравниваемых задач не соответствуют ожидаемому");
        Assertions.assertEquals(taskManager.getSubtasks().getLast().getId(), jsonObject1.get("id").getAsInt(), "ID сравниваемых задач не соответствуют ожидаемому");
    }

    @DisplayName("Получение задачи типа Subtask по ID")
    @Test
    public void getCreatedSubtaskById() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = taskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> subtask2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI taskGetUri = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).GET().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        JsonElement jsonElement = JsonParser.parseString(taskGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Assertions.assertEquals(200, subtask1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(2, taskManager.getSubtasks().size(), "Количество добавленных задач не соответствует ожидаемому");
        Assertions.assertEquals(taskManager.getSubtasks().getFirst().getName(), jsonObject.get("name").getAsString(), "Имена сравниваемых задач не соответствуют ожидаемому");
    }

    @DisplayName("Удаление задачи типа Subtask по ID")
    @Test
    public void deleteCreatedSubtaskById() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = taskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> subtask2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI taskGetUri = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).DELETE().build();

        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(204, taskGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Количество добавленных задач не соответствует ожидаемому");
    }

    @DisplayName("Удаление задачи типа Subtask по ID которого нет")
    @Test
    public void deleteCreatedTasksByUnrealId() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = taskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> subtask2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI taskGetUri = URI.create("http://localhost:8080/subtasks/6");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).DELETE().build();

        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(404, taskGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(2, taskManager.getSubtasks().size(), "Количество добавленных задач не соответствует ожидаемому");
    }

    @DisplayName("Удаление всех задач типа Subtask")
    @Test
    public void deleteAllCreatedSubtask() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now().plusMinutes(15), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest subtask1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> subtask1Response = taskHttpClient.send(subtask1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> subtask2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        URI taskGetUri = URI.create("http://localhost:8080/subtasks");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).DELETE().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(204, taskGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Количество добавленных задач не соответствует ожидаемому");
    }

    @DisplayName("Пересечение задач типа Subtask")
    @Test
    public void intersectionSubtasks() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/subtasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 2, LocalDateTime.now(), 10);
        Task subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 2, LocalDateTime.now(), 11);
        String subtaskJson = gson.toJson(subtask);
        String subtask2Json2 = gson.toJson(subtask2);

        HttpRequest task1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> task1Response = taskHttpClient.send(task1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(subtask2Json2)).build();
        HttpResponse<String> task2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Подзадачи пересекаются по времени");
    }
}


