package com.tasktracker.app.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tasktracker.app.model.Endpoint;
import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Type;
import com.tasktracker.app.service.CustomExeptions.ManagersExep;
import com.tasktracker.app.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GETEPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GETEPIC_ID: {
                handleGetepicID(exchange);
                break;
            }
            case GETEPICSUBTASKS: {
                handleEpicSubtasks(exchange);
                break;
            }
            case CREATEEPIC: {
                handlePostEpic(exchange);
                break;
            }
            case DELETEEPIC: {
                handleDeleteEpic(exchange);
                break;
            }
            case DELETEEPICS: {
                handleDeleteEpics(exchange);
                break;
            }
            default:
                sendNotFoundMessage(exchange);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        removeAllTasks(exchange, Type.EPIC);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        removeTask(exchange, Type.EPIC);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
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
            Epic epicOfELement = gson.fromJson(jsonElement, Epic.class);
            Epic epic = new Epic(epicOfELement.getName(), epicOfELement.getDescription());
            epic.setDuration(epicOfELement.getDuration());
            if (epicOfELement.getId() > 0) {
                epic.setId(epicOfELement.getId());
                Epic updEpic = taskManager.updateEpic(epic);
                String json = gson.toJson(updEpic);
                System.out.println(LocalDateTime.now() + " Задача с ID=" + epic.getId() + " обновлена");
                System.out.println("Ответ сформирован: " + LocalDateTime.now());
                sendMessage(exchange, json);
                System.out.println("Body: " + json);
            } else if (epicOfELement.getId() == 0) {
                Epic newEpic = taskManager.addEpicM(epic);
                System.out.println(LocalDateTime.now() + " Задача с ID = " + epic.getId() + " успешно создана");
                String json = gson.toJson(newEpic);
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
            System.err.println(e.getMessage());
            sendIntersectionFindMessage(exchange);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }


    private void handleEpicSubtasks(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + "Получен запрос - " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            int id = getId(exchange);
            if (taskManager.getEpicId(id) == null) {
                sendNotFoundMessage(exchange);
                return;
            }
            List<Subtask> subtasks = taskManager.returnSubtasksOnEpicId(id);
            String json = gson.toJson(subtasks);
            System.out.println("Ответ сформирован: " + LocalDateTime.now());
            sendMessage(exchange, json);
            System.out.println("Body: " + json);
        } catch (ManagersExep e) {
            System.out.println(e.getMessage());
            sendNotFoundMessage(exchange);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendErrorMessage(exchange);
        }
    }

    private void handleGetepicID(HttpExchange exchange) throws IOException {
        getTaskById(exchange, Type.EPIC);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        getTask(exchange, Type.EPIC);
    }
}
