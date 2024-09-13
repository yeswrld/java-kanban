package com.tasktracker.app.service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void taskManagerInit (){
        taskManager = Managers.getDefault();
    }

    void addTask() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE);
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.DONE);
        Task task4 = new Task("Задача 4", "Описание задачи 4", Status.NEW);
        Task task5 = new Task("Задача 5", "Описание задачи 5", Status.DONE);
        Task task6 = new Task("Задача 6", "Описание задачи 6", Status.DONE);
        Task task7 = new Task("Задача 7", "Описание задачи 7", Status.NEW);
        Task task8 = new Task("Задача 8", "Описание задачи 8", Status.DONE);
        Task task9 = new Task("Задача 9", "Описание задачи 9", Status.DONE);
        Task task10 = new Task("Задача 10", "Описание задачи 10", Status.DONE);
        Task task11 = new Task("Задача 11", "Описание задачи 11", Status.DONE);
        Task task12 = new Task("Задача 12", "Описание задачи 12", Status.DONE);
        Task task13 = new Task("Задача 13", "Описание задачи 13", Status.DONE);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, 14);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addTaskM(task1);
        taskManager.addTaskM(task2);
        taskManager.addTaskM(task3);
        taskManager.addTaskM(task4);
        taskManager.addTaskM(task5);
        taskManager.addTaskM(task6);
        taskManager.addTaskM(task7);
        taskManager.addTaskM(task8);
        taskManager.addTaskM(task9);
        taskManager.addTaskM(task10);
        taskManager.addTaskM(task11);
        taskManager.addTaskM(task12);
        taskManager.addTaskM(task13);
        taskManager.addEpicM(epic1);
        taskManager.addSubTaskM(subtask2);
    }

    @Test
    void taskManagerFilling() { //проверка добавления задач
        addTask();
        Assertions.assertNotNull(taskManager, "Трекер задач пустой");
    }

    @Test
    void checkSizeOfHistoryLess10() { //проверка размерности истории просмотров, и что история просмотров не завышает указанный размер
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        final int checkSize = 12;
        final int APPEAL_TO_HISTORY = 15; //для создания запросов к истории просмотров
        for (int i = 0; i < checkSize; i++) {
            taskManager.addTaskM(task);
        }
        for (int i = 0; i <= APPEAL_TO_HISTORY; i++) {
            taskManager.getTaskId(i);
        }
        final int historySize = taskManager.getHistory().size();
        Assertions.assertTrue(checkSize <= historySize, "Проверяемый размер больше 10, т.к. " +
                "проверяемых значений - " + checkSize);
    }

    @Test
    void getTaskByIdAndHistoryRewriting() { //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
        addTask();
        for (int i = 1; i < 9; i++) {
            taskManager.getTaskId(i);
        }
        taskManager.getEpicId(14);
        taskManager.getSubTaskId(15);
        taskManager.getTaskId(7);
        taskManager.getEpicId(14);
        for (int i = 0; i < 10; i++) {
            System.out.println("Последняя задача " + (i + 1) + " - " + " " + taskManager.getHistory().get(i));
        }

    }


}