package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private int counter;
    protected  final HashMap<Integer, Task> taskM = new HashMap<>();
    protected  final HashMap<Integer, Epic> epicM = new HashMap<>();
    protected  final HashMap<Integer, Subtask> subTaskM = new HashMap<>();

    public TaskManager() {
        this.counter = 0;
    }

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

    public ArrayList<Subtask> printOnEpicId(int epicId) {
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
        subTaskM.remove(id);
    }

    public void removeEpicOnId(int id) {
        epicM.remove(id);
    }

    // обновление статуса эпика
    private void updateEpicStatus(int epicId) {
        ArrayList<Integer> subTaskList = epicM.get(epicId).getSubtaskIdList();
        ArrayList<Subtask> subTaskArrayList = new ArrayList<>();
        for (int id : subTaskList) {
            subTaskArrayList.add(subTaskM.get(id));
        }
        boolean isDone = true;
        boolean isNew = true;

        if (subTaskArrayList.isEmpty()) {
            epicM.get(epicId).setStatus(Status.NEW);
        } else {
            for (Subtask subtask : subTaskArrayList) {
                if (!subtask.getStatus().equals(Status.DONE)) {
                    isDone = false;
                }
                if (!subtask.getStatus().equals(Status.NEW)) {
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

    public void updateEpic (Epic epic){
        if (epic != null && epicM.containsKey(epic.getId())){
            Epic epic1 = epicM.get(epic.getId());
            if (epic1 == null){
                return;
            }
            epic1.setName(epic.getName());
            epic1.setDescription(epic.getDescription());
        }
    }

    public void updateSubstask(Subtask subtask){
        if (subtask != null && subTaskM.containsKey(subtask.getId())){
            subTaskM.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void updateTask (Task task){
        if (task != null && taskM.containsKey(task.getId())){
            int id = task.getId();
            Task task1 = taskM.get(id);
            if (task1 == null) {
                return;
            }
            taskM.put(id, task);
        }
    }

    public void deleteAll(){
        taskM.clear();
        epicM.clear();
        subTaskM.clear();
    }

}
