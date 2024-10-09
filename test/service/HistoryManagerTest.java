package service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.HistoryManager;
import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Проверка HistoryManager")
class HistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();

    void addAllTasks() {
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 1", Status.NEW);
        Epic epic1 = new Epic("Эпик 3", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, 3);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, 3);
        task.setId(1);
        task2.setId(2);
        epic1.setId(3);
        subtask1.setId(4);
        subtask2.setId(5);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
    }

    @DisplayName("Проверяем работоспособность истории просмотров")
    @Test
    void newHistoryManager() { //проверяем работоспособность истории просмотров
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер не работает");
    }

    @DisplayName("Проверка размера истории просмотров до/после удаления")
    @Test
    void checkSizeOfHistory() { //Проверьте, что встроенный связный список версий, а также операции добавления и удаления работают корректно.
        addAllTasks();
        Assertions.assertNotEquals(0, historyManager.getTasks().size());
        System.out.println("Размер истории - " + historyManager.getTasks().size());
        historyManager.remove(5);
        Assertions.assertEquals(4, historyManager.getTasks().size());
        System.out.println("Размер истории после удаления 1 записи  = " + historyManager.getTasks().size());
    }

    @DisplayName("Проверка правильной последовательности заполнения")
    @Test
    void checkPutQuene() {
        addAllTasks();
        Assertions.assertEquals(historyManager.getTasks().getFirst().getName(), "Задача 1");
        Assertions.assertEquals(historyManager.getTasks().get(2).getName(), "Эпик 3");
        Assertions.assertEquals(historyManager.getTasks().getLast().getName(), "Подзадача 2");
    }

    @DisplayName("Проверка изменения описания в задачах")
    @Test
    void changeDescription() {
        addAllTasks();
        historyManager.getTasks().get(2).setDescription("Измененное описание");
        Assertions.assertEquals(historyManager.getTasks().get(2).getDescription(), "Измененное описание");
    }
}