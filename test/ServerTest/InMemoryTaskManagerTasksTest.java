package ServerTest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tasktracker.app.model.Adapters.DurationAdapter;
import com.tasktracker.app.model.Adapters.LocalDateTimeAdapter;
import com.tasktracker.app.model.Status;
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

public class InMemoryTaskManagerTasksTest {
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

    @DisplayName("Добавление задач типа Task")
    @Test
    public void createTask() throws IOException, InterruptedException {
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

        Assertions.assertEquals(200, task1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(2, taskManager.getTasks().size(), "Кол-во задач не соотвествует ожидаемому");
        Assertions.assertEquals("Задача 1", taskManager.getTasks().getFirst().getName(), "Название задачи не соотвествует ожидаемому");

        System.out.println();
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
    }

    @DisplayName("Удаление задач типа Task")
    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
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

        URI delUri = URI.create("http://localhost:8080/tasks/2");
        HttpRequest task1DeleteRequest = HttpRequest.newBuilder().uri(delUri).DELETE().build();
        HttpResponse<String> response = taskHttpClient.send(task1DeleteRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasks());

        Assertions.assertEquals(204, response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Кол-во задач не соотвествует ожидаемому");
        Assertions.assertEquals("Задача 2", taskManager.getTasks().getLast().getName(), "Название задачи не соотвествует ожидаемому");
    }

    @DisplayName("Обновление задач типа Task")
    @Test
    public void updateTaskByID() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/tasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 10);
        Task task2 = new Task("Обновленная задача 1", "Описание задачи 2", Status.NEW, LocalDateTime.now().plusMinutes(15), 11);
        task2.setId(2);
        String task1Json = gson.toJson(task1);
        String task2Json2 = gson.toJson(task2);

        HttpRequest task1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpResponse<String> task1Response = taskHttpClient.send(task1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(task2Json2)).build();
        HttpResponse<String> task2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(200, task1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Кол-во задач не соотвествует ожидаемому");
        Assertions.assertEquals("Обновленная задача 1", taskManager.getTasks().getFirst().getName(), "Название задачи не соотвествует ожидаемому");

        System.out.println();
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

    }

    @DisplayName("Тест BadRequest - 400")
    @Test
    public void sendBadRequestTest() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/tasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();
        String json = "";
        HttpRequest request = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = taskHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Статус код неккоректного запроса не прошёл");

    }

    @DisplayName("Получение всех задач типа Task")
    @Test
    public void getCreatedTasks() throws IOException, InterruptedException {
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

        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskUri).GET().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        JsonElement jsonElement = JsonParser.parseString(taskGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        JsonObject jsonObject1 = jsonElement.getAsJsonArray().get(1).getAsJsonObject();
        Assertions.assertEquals(200, task1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(taskManager.getTasks().get(0).getName(), jsonObject.get("name").getAsString(), "Имена сравниваемых задач не соответствуют ожидаемому");
        Assertions.assertEquals(taskManager.getTasks().getLast().getId(), jsonObject1.get("id").getAsInt(), "ID сравниваемых задач не соответствуют ожидаемому");
    }

    @DisplayName("Получение задачи типа Task по ID")
    @Test
    public void getCreatedTasksById() throws IOException, InterruptedException {
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

        URI taskGetUri = URI.create("http://localhost:8080/tasks/2");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).GET().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        JsonElement jsonElement = JsonParser.parseString(taskGetResponse.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Assertions.assertEquals(200, task1Response.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(2, taskManager.getTasks().size(), "Количество добавленных задач не соответствует ожидаемому");
        Assertions.assertEquals(taskManager.getTasks().getFirst().getName(), jsonObject.get("name").getAsString(), "Имена сравниваемых задач не соответствуют ожидаемому");
    }

    @DisplayName("Удаление задачи типа Task по ID")
    @Test
    public void deleteCreatedTasksById() throws IOException, InterruptedException {
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

        URI taskGetUri = URI.create("http://localhost:8080/tasks/2");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).DELETE().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(204, taskGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Количество добавленных задач не соответствует ожидаемому");
    }

    @DisplayName("Удаление задачи типа Task по ID которого нет")
    @Test
    public void deleteCreatedTasksByUnrealId() throws IOException, InterruptedException {
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

        URI taskGetUri = URI.create("http://localhost:8080/tasks/6");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).DELETE().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(404, taskGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(2, taskManager.getTasks().size(), "Количество добавленных задач не соответствует ожидаемому");
    }

    @DisplayName("Удаление всех задач типа Task")
    @Test
    public void deleteAllCreatedTasks() throws IOException, InterruptedException {
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

        URI taskGetUri = URI.create("http://localhost:8080/tasks");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(taskGetUri).DELETE().build();
        HttpResponse<String> taskGetResponse = taskHttpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());


        Assertions.assertEquals(204, taskGetResponse.statusCode(), "Статус код не соответствует ожидаемому");
        Assertions.assertEquals(0, taskManager.getTasks().size(), "Количество добавленных задач не соответствует ожидаемому");
    }

    @DisplayName("Пересечение задач типа Task")
    @Test
    public void intersectionTest() throws IOException, InterruptedException {
        URI taskUri = URI.create("http://localhost:8080/tasks");
        HttpClient taskHttpClient = HttpClient.newHttpClient();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(), 10);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW, LocalDateTime.now(), 11);
        String task1Json = gson.toJson(task1);
        String task2Json2 = gson.toJson(task2);

        HttpRequest task1HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpResponse<String> task1Response = taskHttpClient.send(task1HttpRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest task2HttpRequest = HttpRequest.newBuilder().uri(taskUri).POST(HttpRequest.BodyPublishers.ofString(task2Json2)).build();
        HttpResponse<String> task2Response = taskHttpClient.send(task2HttpRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, task2Response.statusCode());
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Задачи пересекаются по времени");
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size(), "Задачи пересекаются по времени");
    }
}


