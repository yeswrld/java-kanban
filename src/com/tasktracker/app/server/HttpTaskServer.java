package com.tasktracker.app.server;

import com.sun.net.httpserver.HttpServer;
import com.tasktracker.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final TaskManager manager;
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        manager = taskManager;
    }

    public void serverStart() throws IOException {

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PriorityTaskHandler(manager));
        server.start();
        System.out.println("HTTP сервер запущен на " + PORT + " порту.");
    }

    public void stopServer() {
        server.stop(0);
    }

}
