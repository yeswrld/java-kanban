package com.tasktracker.app.service;

import com.tasktracker.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int HISTORY_MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_MAX_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

}