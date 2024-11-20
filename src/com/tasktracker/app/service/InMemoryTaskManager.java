package com.tasktracker.app.service;

import com.tasktracker.app.model.Epic;
import com.tasktracker.app.model.Status;
import com.tasktracker.app.model.Subtask;
import com.tasktracker.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskM = new HashMap<>();
    protected final Map<Integer, Epic> epicM = new HashMap<>();
    protected final Map<Integer, Subtask> subTaskM = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    protected int counter = 1;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task addTaskM(Task task) {
        if (intersection(task)) {
            throw new RuntimeException(LocalDateTime.now() + "Задача не может быть создана или обновлена, т.к. пересекается с существующей");
        }
        int taskCount = generateCounter();
        task.setId(taskCount);
        taskM.put(taskCount, task);
        updatePriorityTasks(task);
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
        if (intersection(subtask)) {
            throw new RuntimeException(LocalDateTime.now() + " Задача с ИД = " + subtask.getId() + " может быть создана или обновлена, т.к. пересекается с существующей");

        }
        Epic epic = epicM.get(subtask.getEpicId());
        updatePriorityTasks(subtask);
        if (epic != null) {
            subTaskM.put(subTaskCount, subtask);
            epic.addSubtaskId(subTaskCount);
            updateEpicStatus(epic.getId());
            updateEpicTimesAndDuration(epic);
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
        } else if (task == null) {
            throw new RuntimeException("Таск с ИД = " + id + " не найден");
        }
        return task;
    }

    @Override
    public Subtask getSubTaskId(int id) {
        final Subtask subtask = subTaskM.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        } else if (subtask == null) {
            throw new RuntimeException("Субтаск с ИД = " + id + " не найден");
        }
        return subtask;
    }

    @Override
    public Epic getEpicId(int id) {
        final Epic epic = epicM.get(id);
        if (epic != null) {
            historyManager.add(epic);
        } else if (epic == null) {
            throw new RuntimeException("Эпик с ИД = " + id + " не найден");
        }
        return epic;
    }

    @Override
    public List<Subtask> returnSubtasksOnEpicId(int epicId) {
        List<Integer> subtaskIdList = epicM.get(epicId).getSubtaskIdList();
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        return epicM.get(epicId).getSubtaskIdList().stream()
                .map(subTaskM::get)
                .collect(Collectors.toList());
    }

    @Override
    public void removeTaskOnId(int id) {
        if (taskM.get(id) == null) {
            throw new RuntimeException(LocalDateTime.now() + "Задача не может быть удалена, т.к. задача с ИД = " + id + " не найдена");
        }
        removePriorityTask(taskM.get(id));
        taskM.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTaskOnId(int id) {
        int epicId = subTaskM.get(id).getEpicId();
        if (subTaskM.get(id) == null) {
            throw new RuntimeException(LocalDateTime.now() + "Задача не может быть удалена, т.к. задача с ИД = " + id + " не найдена");
        }
        epicM.get(epicId).deleteEpicSubtask(id);
        removePriorityTask(subTaskM.get(id));
        subTaskM.remove(id);
        updateEpicStatus(epicId);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicOnId(int id) {
        final Epic epic = epicM.get(id);
        historyManager.remove(id);
        epic.getSubtaskIdList().forEach(subtaskId -> {
            prioritizedTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            subTaskM.remove(subtaskId);
        });
        prioritizedTasks.remove(epic);
        subTaskM.remove(epic);
        epicM.remove(id);
    }


    @Override
    public Epic updateEpic(Epic epic) {
        if (epic != null && epicM.containsKey(epic.getId())) {
            Epic savedEpic = epicM.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public Subtask updateSubstask(Subtask subtask) {
        if (intersection(subtask)) {
            return null;
        }
        if (subtask != null && subTaskM.containsKey(subtask.getId())) {
            subTaskM.put(subtask.getId(), subtask);
            Epic epic = epicM.get(subtask.getEpicId());
            updateEpicTimesAndDuration(epic);
            updatePriorityTasks(subtask);
            return subtask;
        }
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        if (intersection(task)) {
            throw new RuntimeException(LocalDateTime.now() + "Задача не может быть создана или обновлена, т.к. пересекается с существующей");
        }
        if (task != null && taskM.containsKey(task.getId())) {
            int id = task.getId();
            taskM.put(id, task);
            updatePriorityTasks(task);
        }
        return task;
    }

    @Override
    public void deleteSubtasks() {
        subTaskM.keySet().stream()
                .forEach(subtaskForDelete -> {
                    removePriorityTask(subTaskM.get(subtaskForDelete));
                    historyManager.remove(subtaskForDelete);
                });
        subTaskM.clear();
        epicM.values().forEach(epic -> {
            epic.clearSubtaskMapIdList();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public void deleteTasks() {
        taskM.keySet().forEach(task -> {
            historyManager.remove(task);
            removePriorityTask(taskM.get(task));
        });
        taskM.clear();

    }

    @Override
    public void deleteEpics() {
        epicM.keySet().stream()
                .forEach(epicForDelete -> historyManager.remove(epicForDelete));
        subTaskM.keySet().stream()
                .forEach(subtaskForDelete -> historyManager.remove(subtaskForDelete));
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

    protected void updateEpicTimesAndDuration(Epic epic) {
        updateEpicStatus(epic.getId());
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        updateEpicEndTime(epic);
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

    // обновление длительности эпика
    private void updateEpicDuration(Epic epic) {
        Duration duration = Duration.ZERO;
        for (Subtask subtask : returnSubtasksOnEpicId(epic.getId())) {
            if (subtask.getDuration() != null && !subtask.getDuration().isZero()) {
                Duration subtask1Duration = subtask.getDuration();
                duration = duration.plus(subtask1Duration);
            }
        }
        epic.setDuration(duration);
    }

    //обновление времени начала эпика
    private void updateEpicStartTime(Epic epic) {
        Optional<Subtask> epicStartTime = returnSubtasksOnEpicId(epic.getId()).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Task::getStartTime));
        epicStartTime.ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));
    }

    //обновление времени окончания эпика
    private void updateEpicEndTime(Epic epic) {
        Optional<Subtask> epicEndTime = returnSubtasksOnEpicId(epic.getId()).stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Task::getEndTime));
        epicEndTime.ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
    }

    private void updatePriorityTasks(Task task) {
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }

    private void removePriorityTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    private boolean intersection(Task task) {
        if (task == null || task.getStartTime() == null) {
            return false;
        }
        Optional<Task> intersec = getPrioritizedTasks().stream()
                .filter(task1 -> task1.getId() != task.getId())
                .filter(task1 -> task.getStartTime().isBefore(task1.getEndTime())
                        && task.getEndTime().isAfter(task1.getStartTime()))
                .findFirst();

        return intersec.isPresent();
    }

}
