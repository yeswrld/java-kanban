package com.tasktracker.app.service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    protected int counter = 1;
    protected final Map<Integer, Task> taskM = new HashMap<>();
    protected final Map<Integer, Epic> epicM = new HashMap<>();
    protected final Map<Integer, Subtask> subTaskM = new HashMap<>();
    private final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    @Override
    public Task addTaskM(Task task) {
        int taskCount = generateCounter();
        task.setId(taskCount);
        taskM.put(taskCount, task);
        return task;
    }

    @Override
    public Epic addEpicM(Epic epic) {
        int epicCount = generateCounter();
        epic.setId(epicCount);
        epicM.put(epicCount, epic);
        return epic;
    }

    @Override
    public Subtask addSubTaskM(Subtask subtask) {
        int subTaskCount = generateCounter();
        subtask.setId(subTaskCount);
        Epic epic = epicM.get(subtask.getEpicId());
        if (epic != null) {
            subTaskM.put(subTaskCount, subtask);
            epic.addSubtaskId(subTaskCount);
            updateEpicStatus(epic.getId());
            return subtask;
        }
        return subtask;
    }

    @Override
    public List<Task> getTasks() {

        return new ArrayList<>(taskM.values());
    }

    @Override
    public List<Task> getSubtasks() {
        return new ArrayList<>(subTaskM.values());
    }

    @Override
    public List<Task> getEpics() {
        return new ArrayList<>(epicM.values());
    }

    @Override
    public Task getTaskId(int id) {
        final Task task = taskM.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubTaskId(int id) {
        final Subtask subtask = subTaskM.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicId(int id) {
        final Epic epic = epicM.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public List<Subtask> returnSubtasksOnEpicId(int epicId) {
        List<Integer> subtaskIdList = epicM.get(epicId).getSubtaskIdList();
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        for (int subtaskId : subtaskIdList) {
            subtaskArrayList.add(subTaskM.get(subtaskId));
        }
        return subtaskArrayList;
    }

    @Override
    public void removeTaskOnId(int id) {
        taskM.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskOnId(int id) {
        int epicId = subTaskM.get(id).getEpicId();
        epicM.get(epicId).deleteEpicSubtask(id);
        subTaskM.remove(id);
        updateEpicStatus(epicId);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicOnId(int id) {
        final Epic epic = epicM.get(id);
        historyManager.remove(id);
        for (Integer subtaskId : epic.getSubtaskIdList()) {
            historyManager.remove(subtaskId);
            subTaskM.remove(subtaskId);
        }
        subTaskM.remove(epic);
        epicM.remove(id);
    }


    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epicM.containsKey(epic.getId())) {
            Epic savedEpic = epicM.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubstask(Subtask subtask) {
        if (subtask != null && subTaskM.containsKey(subtask.getId())) {
            subTaskM.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && taskM.containsKey(task.getId())) {
            int id = task.getId();
            taskM.put(id, task);
        }
    }

    @Override
    public void deleteSubtasks() {
        for (int subtaskForDelete : subTaskM.keySet()) {
            historyManager.remove(subtaskForDelete);
        }
        subTaskM.clear();
        for (Epic epic : epicM.values()) {
            epic.clearSubtaskMapIdList();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTasks() {
        for (int taskForDelete : taskM.keySet()) {
            historyManager.remove(taskForDelete);
        }
        taskM.clear();
    }

    @Override
    public void deleteEpics() {
        for (int epicForDelete : epicM.keySet()) {
            historyManager.remove(epicForDelete);
        }
        for (int subtaskForDelete : subTaskM.keySet()) {
            historyManager.remove(subtaskForDelete);
        }
        epicM.clear();
        subTaskM.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getTasks();
    }

    private int generateCounter() {
        return ++counter;
    }

    // обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        final Epic epic = epicM.get(epicId);
        List<Integer> subTaskList = epic.getSubtaskIdList();
        boolean isDone = true;
        boolean isNew = true;

        if (subTaskList.isEmpty()) {
            epicM.get(epicId).setStatus(Status.NEW);
        } else {
            for (int subtaskId : subTaskList) {
                final Status status = subTaskM.get(subtaskId).getStatus();
                if (!status.equals(Status.DONE)) {
                    isDone = false;
                }
                if (!status.equals(Status.NEW)) {
                    isNew = false;
                }
                if (!isDone && !isNew) {
                    break;
                }

            }
            if (isDone) {
                epicM.get(epicId).setStatus(Status.DONE);
            } else if (isNew) {
                epicM.get(epicId).setStatus(Status.NEW);
            } else {
                epicM.get(epicId).setStatus(Status.IN_PROGRESS);
            }
        }
    }

}
