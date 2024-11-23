package com.tasktracker.app;

import com.tasktracker.app.server.HttpTaskServer;
import com.tasktracker.app.service.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        try {
            server.serverStart();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("HELLO");
    }
}
