package com.tasktracker.app.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.tasktracker.app.model.Adapters.DurationAdapter;
import com.tasktracker.app.model.Adapters.LocalDateTimeAdapter;
import com.tasktracker.app.model.Endpoint;
import com.tasktracker.app.model.Type;
import com.tasktracker.app.service.ManagersExep;
import com.tasktracker.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BaseHandler {
    protected TaskManager taskManager;
    protected Gson gson = new Gson().newBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    protected Endpoint getEndpoint(String requestPath, String requestMetod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (pathParts[1].equals("tasks") && requestMetod.equals("GET")) {
                return Endpoint.GETTASKS;
            } else if (pathParts[1].equals("tasks") && requestMetod.equals("POST")) {
                return Endpoint.CREATETASK;
            } else if (pathParts[1].equals("tasks") && requestMetod.equals("DELETE")) {
                return Endpoint.DELETETASKS;
            } else if ((pathParts[1].equals("subtasks") && requestMetod.equals("GET"))) {
                return Endpoint.GETSUBTASKS;
            } else if ((pathParts[1].equals("subtasks") && requestMetod.equals("POST"))) {
                return Endpoint.CREATESUBTASK;
            } else if ((pathParts[1].equals("subtasks") && requestMetod.equals("DELETE"))) {
                return Endpoint.DELETESUBTASKS;
            } else if ((pathParts[1].equals("epics") && requestMetod.equals("GET"))) {
                return Endpoint.GETEPICS;
            } else if ((pathParts[1].equals("epics") && requestMetod.equals("POST"))) {
                return Endpoint.CREATEEPIC;
            } else if ((pathParts[1].equals("epics") && requestMetod.equals("DELETE"))) {
                return Endpoint.DELETEEPICS;
            } else if ((pathParts[1].equals("prioritized") && requestMetod.equals("GET"))) {
                return Endpoint.GETPRIORITYTASKS;
            }
        } else if (pathParts.length == 3) {
            if (pathParts[1].equals("tasks") && requestMetod.equals("GET")) {
                return Endpoint.GETTASKBYID;
            } else if (pathParts[1].equals("tasks") && requestMetod.equals("DELETE")) {
                return Endpoint.DELETETASK;
            } else if (pathParts[1].equals("subtasks") && requestMetod.equals("GET")) {
                return Endpoint.CREATESUBTASK_ID;
            } else if (pathParts[1].equals("subtasks") && requestMetod.equals("DELETE")) {
                return Endpoint.DELETESUBTASK;
            } else if (pathParts[1].equals("epics") && requestMetod.equals("GET")) {
                return Endpoint.GETEPIC_ID;
            } else if (pathParts[1].equals("epics") && requestMetod.equals("DELETE")) {
                return Endpoint.DELETEEPIC;
            }
        } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks") && requestMetod.equals("GET")) {
            return Endpoint.GETEPICSUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    protected void getTask(HttpExchange exchange, Type taskType) throws IOException {
        System.out.println(LocalDate.now() + " Получен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            String json = switch (taskType) {
                case EPIC -> gson.toJson(taskManager.getEpics());
                case TASK -> gson.toJson(taskManager.getTasks());
                case SUBTASK -> gson.toJson(taskManager.getSubtasks());
            };
            System.out.println("Ответ сформирован: " + LocalDateTime.now());
            sendMessage(exchange, json);
            System.out.println("Body: " + json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }

    protected void getTaskById(HttpExchange exchange, Type taskType) throws IOException {
        System.out.println(LocalDateTime.now() + " Получен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            int id = getId(exchange);
            String json = switch (taskType) {
                case EPIC -> gson.toJson(taskManager.getEpicId(id));
                case SUBTASK -> gson.toJson(taskManager.getSubTaskId(id));
                case TASK -> gson.toJson(taskManager.getTaskId(id));
            };
            System.out.println("Ответ сформирован: " + LocalDateTime.now());
            sendMessage(exchange, json);
            System.out.println("Body " + json);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            sendNotFoundMessage(exchange);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }

    protected void removeTask(HttpExchange exchange, Type taskType) throws IOException {
        System.out.println(LocalDateTime.now() + " Полуучен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            int id = getId(exchange);
            switch (taskType) {
                case EPIC -> taskManager.removeEpicOnId(id);
                case TASK -> taskManager.removeTaskOnId(id);
                case SUBTASK -> taskManager.removeSubTaskOnId(id);
            }
            System.out.println("Ответ сформирован: " + LocalDateTime.now());
            sendOkMessage(exchange);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            sendNotFoundMessage(exchange);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }

    protected void removeAllTasks(HttpExchange exchange, Type taskType) throws IOException {
        System.out.println(LocalDateTime.now() + " Получен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            switch (taskType) {
                case EPIC -> taskManager.deleteEpics();
                case TASK -> taskManager.deleteTasks();
                case SUBTASK -> taskManager.deleteSubtasks();
            }
            System.out.println("Ответ сформирован: " + LocalDateTime.now());
            sendOkMessage(exchange);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }

    protected void sendOkMessage(HttpExchange exchange) throws IOException {
        int statusCode = 204;
        exchange.sendResponseHeaders(statusCode, -1);
        exchange.close();
        System.out.println("HTTP code: " + statusCode);
        System.out.println(exchange.getRequestHeaders());
    }

    protected int getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            throw new ManagersExep(LocalDateTime.now() + "Некоректный ID задачи");
        }
    }

    protected void sendMessage(HttpExchange exchange, String message) throws IOException {
        int statusCode = 200;
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
        System.out.println("HTTP code: " + statusCode);
        System.out.println(exchange.getRequestHeaders());
    }

    protected void sendErrorMessage(HttpExchange exchange) throws IOException {
        int statusCode = 500;
        exchange.sendResponseHeaders(statusCode, 0);
        System.out.println(LocalDateTime.now() + " statusCode: " + statusCode);
        System.out.println(exchange.getResponseHeaders());
        exchange.close();
    }

    protected void sendBadRequestMessage(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " Неккоректный запрос");
        exchange.sendResponseHeaders(400, 0);
        System.out.println(exchange.getResponseHeaders());
        exchange.close();
    }

    protected void sendNotFoundMessage(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendIntersectionFindMessage(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }

    protected String readRequestFromBody(HttpExchange exchange) throws IOException {
        String body;
        try (InputStream inputStream = exchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        System.out.println(LocalDateTime.now() + " Получен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI() + "Body :" + body);
        if (body.isBlank()) {
            sendBadRequestMessage(exchange);
            return " ";
        }
        return body;
    }

}
