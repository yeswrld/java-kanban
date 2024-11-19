package com.tasktracker.app.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tasktracker.app.model.Endpoint;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.model.Type;
import com.tasktracker.app.service.ManagersExep;
import com.tasktracker.app.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class TaskHandler extends BaseHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GETTASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GETTASKBYID: {
                handleGetTaskById(exchange);
                break;
            }
            case CREATETASK: {
                handleCreateTask(exchange);
                break;
            }
            case DELETETASK: {
                handleRemoveTask(exchange);
                break;
            }
            case DELETETASKS: {
                handleRemoveTasks(exchange);
                break;
            }
        }
    }

    private void handleRemoveTasks(HttpExchange exchange) throws IOException {
        removeAllTasks(exchange, Type.TASK);
    }

    private void handleRemoveTask(HttpExchange exchange) throws IOException {
        removeTask(exchange, Type.TASK);
    }


    private void handleCreateTask(HttpExchange exchange) throws IOException {
        String body = readRequestFromBody(exchange);
        if (body.isEmpty()) {
            return;
        }
        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequestMessage(exchange);
                return;
            }

            Task taskOfElement = gson.fromJson(jsonElement, Task.class);
            Task task = new Task(taskOfElement.getName(), taskOfElement.getDescription(),
                    taskOfElement.getStatus(),
                    taskOfElement.getStartTime(),
                    (int) taskOfElement.getDuration().toMinutes());

            if (taskOfElement.getId() > 0) {
                task.setId(taskOfElement.getId());
                task.setStatus(taskOfElement.getStatus());
                task.setStartTime(taskOfElement.getStartTime());
                task.setDuration(taskOfElement.getDuration());
                Task updTask = taskManager.updateTask(task);
                String json = gson.toJson(updTask);
                System.out.println(LocalDateTime.now() + " Задача с ID=" + task.getId() + " обновлена");
                System.out.println("Ответ сформирован: " + LocalDateTime.now());
                sendMessage(exchange, json);
                System.out.println("Body: " + json);
            } else if (task.getId() == 0) {
                Task newTask = taskManager.addTaskM(task);
                System.out.println(LocalDateTime.now() + " Задача с ID = " + task.getId() + " успешно создана");
                String json = gson.toJson(newTask);
                System.out.println("Ответ сформирован: " + LocalDateTime.now());
                sendMessage(exchange, json);
                System.out.println("Body: " + json);
            } else {
                sendNotFoundMessage(exchange);
            }
        } catch (ManagersExep e) {
            System.out.println(e.getMessage());
            sendNotFoundMessage(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        getTask(exchange, Type.TASK);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        getTaskById(exchange, Type.TASK);
    }
}
