package com.tasktracker.app.service;

public class Managers {
    public static InMemoryTaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return (HistoryManager) new InMemoryHistoryManager();
    }
}
