package com.tasktracker.app.service;

import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Проверка HistoryManager")
class HistoryManagerTest {

    @DisplayName("Проверяем историю просмотров")
    @Test
    void addToHistory() { //проверяем добавление в историю просмотров
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        historyManager.add(task);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История пустая/не соответсвует ожидаемому значению");
    }

    @DisplayName("Проверяем работоспособность истории просмотров")
    @Test
    void newHistoryManager() { //проверяем работоспособность истории просмотров
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер не работает");
    }

    @DisplayName("Проверка размерности истории просмотров")
    @Test
    void checkSizeOfHistoryLess10() { //проверка размерности истории просмотров, и что история просмотров не завышает указанный размер
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        final int checkSize = 9;
        for (int i = 0; i < checkSize; i++) {
            historyManager.add(task);
        }
        List<Task> requestList = historyManager.getHistory();
        assertEquals(checkSize, requestList.size(), "Проверяемый размер больше 10, т.к. " +
                "проверяемый размер - " + checkSize);
    }
}