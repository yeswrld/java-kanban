package com.tasktracker.app.service;

import com.tasktracker.app.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

}
