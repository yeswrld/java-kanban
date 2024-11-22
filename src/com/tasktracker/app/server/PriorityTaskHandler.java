package com.tasktracker.app.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tasktracker.app.model.Endpoint;
import com.tasktracker.app.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class PriorityTaskHandler extends BaseHandler implements HttpHandler {

    public PriorityTaskHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        if (Objects.requireNonNull(endpoint) == Endpoint.GETPRIORITYTASKS) {
            handleGetPriorityTasks(exchange);
        } else {
            sendNotFoundMessage(exchange);
        }
    }

    private void handleGetPriorityTasks(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + "Получен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        String json = gson.toJson(taskManager.getPrioritizedTasks());
        System.out.println("Ответ сформирован: " + LocalDateTime.now());
        sendMessage(exchange, json);
        System.out.println("Body: " + json);
    }
}
