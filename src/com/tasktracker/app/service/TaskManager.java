package com.tasktracker.app.service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    Task addTaskM(Task task);

    Epic addEpicM(Epic epic);

    Subtask addSubTaskM(Subtask subtask);

    List<Task> getTasks();

    List<Task> getSubtasks();

    List<Task> getEpics();

    Task getTaskId(int id);

    Subtask getSubTaskId(int id);

    Epic getEpicId(int id);

    List<Subtask> returnSubtasksOnEpicId(int epicId);

    void removeTaskOnId(int id);

    void removeSubTaskOnId(int id);

    void removeEpicOnId(int id);

    Epic updateEpic(Epic epic);

    Subtask updateSubstask(Subtask subtask);

    Task updateTask(Task task);

    void deleteSubtasks();

    void deleteTasks();

    void deleteEpics();

    List<Task> getHistory();

}
