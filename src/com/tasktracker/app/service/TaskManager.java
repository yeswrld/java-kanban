package com.tasktracker.app.service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int addTaskM(Task task);

    int addEpicM(Epic epic);

    int addSubTaskM(Subtask subtask);

    List<Task> getTask();

    ArrayList<Task> printSubtask();

    ArrayList<Task> printEpic();

    Task getTaskId(int id);

    Subtask getSubTaskId(int id);

    Epic getEpicId(int id);

    ArrayList<Subtask> returnSubtasksOnEpicId(int epicId);

    void removeTaskOnId(int id);

    void removeSubTaskOnId(int id);

    void removeEpicOnId(int id);

    // обновление статуса эпика
    void updateEpicStatus(int epicId);

    void updateEpic(Epic epic);

    void updateSubstask(Subtask subtask);

    void updateTask(Task task);

    void deleteSubtasks();

    void deleteTasks();

    void deleteEpics();

    List<Task> getHistory();

    void add(Task task);
}
