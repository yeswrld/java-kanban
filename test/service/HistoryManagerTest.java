package service;

import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.service.HistoryManager;
import com.tasktracker.app.service.Managers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Проверка HistoryManager")
class HistoryManagerTest {


    @DisplayName("Проверяем работоспособность истории просмотров")
    @Test
    void newHistoryManager() { //проверяем работоспособность истории просмотров
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер не работает");
    }

    @DisplayName("Проверка размерности истории просмотров")
    @Test
    void checkSizeOfHistoryLess10() { //Проверьте, что встроенный связный список версий, а также операции добавления и удаления работают корректно.
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 1", Status.NEW);
        Task task3 = new Task("Задача 3", "Описание задачи 1", Status.NEW);
        Task task4 = new Task("Задача 4", "Описание задачи 1", Status.NEW);
        Task task5 = new Task("Задача 5", "Описание задачи 1", Status.NEW);
        Task task6 = new Task("Задача 6", "Описание задачи 1", Status.NEW);
        final int checkSize = 9;
        task.setId(1);
        task2.setId(2);
        task3.setId(3);
        task4.setId(4);
        task5.setId(5);
        task6.setId(6);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        System.out.println("Список в истории до удаления элементов");
        for (int i = 0; i < historyManager.getTasks().size(); i++) {
            System.out.println(historyManager.getTasks().get(i));
        }
        System.out.println("Размер истории до удаления - " + historyManager.getTasks().size());
        System.out.println();
        System.out.println("Список в истории после удаления 1 и 4 элемента");
        historyManager.remove(1);
        historyManager.remove(4);
        for (int i = 0; i < historyManager.getTasks().size(); i++) {
            System.out.println(historyManager.getTasks().get(i));
        }

    }
}