package com.tasktracker.app.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tasktracker.app.model.Endpoint;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Type;
import com.tasktracker.app.service.CustomExeptions.ManagersExep;
import com.tasktracker.app.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHandler implements HttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GETSUBTASKS: {
                handleGetSubtask(exchange);
                break;
            }
            case CREATESUBTASK_ID: {
                handleSubtaskID(exchange);
                break;
            }
            case CREATESUBTASK: {
                handleCreateSubtask(exchange);
                break;
            }
            case DELETESUBTASK: {
                handleDeleteSubtask(exchange);
                break;
            }
            case DELETESUBTASKS: {
                handleDeleteSubtasks(exchange);
                break;
            }
            default:
                sendNotFoundMessage(exchange);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        removeAllTasks(exchange, Type.SUBTASK);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        removeTask(exchange, Type.SUBTASK);
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        String body = readRequestFromBody(exchange);
        if (body.isEmpty()) {
            sendBadRequestMessage(exchange);
            return;
        }
        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequestMessage(exchange);
                return;
            }
            Subtask subtaskOfRequest = gson.fromJson(jsonElement, Subtask.class);
            Subtask subtask = new Subtask(subtaskOfRequest.getName(),
                    subtaskOfRequest.getDescription(),
                    subtaskOfRequest.getStatus(),
                    subtaskOfRequest.getEpicId(),
                    subtaskOfRequest.getStartTime(),
                    (int) subtaskOfRequest.getDuration().toMinutes());
            if (subtaskOfRequest.getId() > 0) {
                subtask.setId(subtaskOfRequest.getId());
                subtask.setDescription(subtaskOfRequest.getDescription());
                subtask.setStatus(subtaskOfRequest.getStatus());
                subtask.setStartTime(subtaskOfRequest.getStartTime());
                subtask.setDuration(subtaskOfRequest.getDuration());
                Subtask updSubtask = taskManager.updateSubstask(subtask);
                String json = gson.toJson(updSubtask);
                System.out.println(LocalDateTime.now() + " Задача с ID=" + subtask.getId() + " обновлена");
                System.out.println("Ответ сформирован: " + LocalDateTime.now());
                sendMessage(exchange, json);
                System.out.println("Body: " + json);
            } else if (subtaskOfRequest.getId() == 0) {
                Subtask newSubtask = taskManager.addSubTaskM(subtask);
                System.out.println(LocalDateTime.now() + " Задача с ID = " + subtask.getId() + " успешно создана");
                String json = gson.toJson(newSubtask);
                System.out.println("Ответ сформирован: " + LocalDateTime.now());
                sendMessage(exchange, json);
                System.out.println("Body: " + json);
            } else {
                sendNotFoundMessage(exchange);
            }
        } catch (ManagersExep e) {
            System.out.println(e.getMessage());
            sendNotFoundMessage(exchange);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            sendIntersectionFindMessage(exchange);
        }
    }

    private void handleSubtaskID(HttpExchange exchange) throws IOException {
        getTaskById(exchange, Type.SUBTASK);
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        getTask(exchange, Type.SUBTASK);
    }
}
