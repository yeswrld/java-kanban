package com.tasktracker.app.service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private int counter;
    private final HashMap<Integer, Task> taskM = new HashMap<>();
    private final HashMap<Integer, Epic> epicM = new HashMap<>();
    private final HashMap<Integer, Subtask> subTaskM = new HashMap<>();

    private int generateCounter() {
        return ++counter;
    }

    public void addTaskM(Task task) {
        if (task != null) {
            int taskCount = generateCounter();
            task.setId(taskCount);
            taskM.put(taskCount, task);
        }
    }

    public void addEpicM(Epic epic) {
        if (epic != null) {
            int epicCount = generateCounter();
            epic.setId(epicCount);
            epicM.put(epicCount, epic);
        }
    }

    public void addSubTaskM(Subtask subtask) {
        if (subtask != null) {
            int subTaskCount = generateCounter();
            subtask.setId(subTaskCount);
            subTaskM.put(subTaskCount, subtask);
            int epicId = subtask.getEpicId();
            epicM.get(epicId).addSubtaskId(subTaskCount);
            updateEpicStatus(epicId);
        }
    }

    public ArrayList<Task> printTask() {
        if (taskM.isEmpty()) {
            System.out.println("Cписок задач пуст");
        }
        return new ArrayList<>(taskM.values());
    }

    public ArrayList<Task> printSubtask() {
        if (subTaskM.isEmpty()) {
            System.out.println("Список подзадач пуст");
        }
        return new ArrayList<>(subTaskM.values());
    }

    public ArrayList<Task> printEpic() {
        if (epicM.isEmpty()) {
            System.out.println("Список эпиков пуст");
        }
        return new ArrayList<>(epicM.values());
    }

    public Task getTaskId(int id) {
        return taskM.get(id);
    }

    public Subtask getSubTaskId(int id) {
        return subTaskM.get(id);
    }

    public Epic getEpicId(int id) {
        return epicM.get(id);
    }

    public ArrayList<Subtask> returnSubtasksOnEpicId(int epicId) {
        ArrayList<Integer> subtaskIdList = epicM.get(epicId).getSubtaskIdList();
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        for (int i : subtaskIdList) {
            subtaskArrayList.add(subTaskM.get(i));
        }
        return subtaskArrayList;
    }

    public void removeTaskOnId(int id) {
        taskM.remove(id);
    }

    public void removeSubTaskOnId(int id) {
        int epicId = subTaskM.get(id).getEpicId();
        epicM.get(epicId).deleteEpicSubtask(id);
        subTaskM.remove(id);
        updateEpicStatus(epicId);
    }

    public void removeEpicOnId(int id) {
        for (Integer ID : epicM.get(id).getSubtaskIdList()){
            subTaskM.remove(ID);
        }
        epicM.remove(id);
    }

    // обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        final Epic epic = epicM.get(epicId);
        ArrayList<Integer> subTaskList = epic.getSubtaskIdList();
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

    public void updateEpic(Epic epic) {
        if (epic != null && epicM.containsKey(epic.getId())) {
            Epic epic1 = epicM.get(epic.getId());
            epic1.setName(epic.getName());
            epic1.setDescription(epic.getDescription());
        }
    }

    public void updateSubstask(Subtask subtask) {
        if (subtask != null && subTaskM.containsKey(subtask.getId())) {
            subTaskM.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void updateTask(Task task) {
        if (task != null && taskM.containsKey(task.getId())) {
            int id = task.getId();
            taskM.put(id, task);
        }
    }

    public void deleteSubtasks (){
        subTaskM.clear();
        for (Epic epic: epicM.values()){
            epic.clearSubtaskMapIdList();
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteTasks (){
        taskM.clear();
    }

    public void deleteEpics (){
        epicM.clear();
        subTaskM.clear();
    }

}
