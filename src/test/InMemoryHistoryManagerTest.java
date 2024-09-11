package test;

import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.HistoryManager;
import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void newHistoryManager() {
        Assertions.assertNotNull(historyManager, "Менеджер исторри не работает");
    }

    @Test
    void checkSizeOfHistory() {
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        final int requestSize = 1;
        final int checkSize = 15;
        for (int i = 0; i <= checkSize ; i++) {
            historyManager.add(task);
        }
        List<Task> listOfHistory = historyManager.getHistory();
        Assertions.assertEquals(requestSize, listOfHistory.size(), "Ограничение не работает");
    }

    @Test
    void add() {
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не пустая");
        Assertions.assertEquals(1, history.size(), "История не пустая");
    }
}