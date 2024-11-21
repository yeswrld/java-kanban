package com.tasktracker.app.service.CustomExeptions;

public class NotFoundExep extends RuntimeException {
    public NotFoundExep(String message) {
        super(message);
    }
}
