package com.tasktracker.app.service.CustomExeptions;

public class ManagersExep extends RuntimeException {
    public ManagersExep(String message) {
        super(message);
    }
}
