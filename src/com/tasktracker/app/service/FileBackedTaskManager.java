package com.tasktracker.app.service;

import com.tasktracker.app.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEAD = "id,type,name,status,description,epic, startTime, duration\n";
    private final File file;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }


    public static FileBackedTaskManager load(HistoryManager historyManager, File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager, file);
        fileBackedTaskManager.load(file);
        return fileBackedTaskManager;
    }

    @Override
    public Task addTaskM(Task task) {
        super.addTaskM(task);
        save();
        return task;
    }

    @Override
    public Epic addEpicM(Epic epic) {
        super.addEpicM(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubTaskM(Subtask subtask) {
        super.addSubTaskM(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Subtask updateSubstask(Subtask subtask) {
        super.updateSubstask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void removeEpicOnId(int id) {
        super.removeEpicOnId(id);
        save();
    }

    @Override
    public void removeSubTaskOnId(int id) {
        super.removeSubTaskOnId(id);
        save();
    }

    @Override
    public void removeTaskOnId(int id) {
        super.removeTaskOnId(id);
        save();
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(HEAD);
            for (Task task : getTasks()) {
                bufferedWriter.write(toString(task));
            }
            for (Task task : getEpics()) {
                bufferedWriter.write(toString(task));
            }
            for (Task task : getSubtasks()) {
                bufferedWriter.write(toString(task));
            }
        } catch (IOException e) {
            throw new ManagersExep("Ошибка записи в файл");
        }
    }

    public void load(File file) {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isEmpty()) continue;
                Task task = stringToTask(line);
                if (counter < task.getId()) {
                    counter = task.getId();
                }
                if (task.getType().equals(Type.TASK)) {
                    taskM.put(task.getId(), task);
                    prioritizedTasks.add(task);

                } else if (task.getType().equals(Type.EPIC)) {
                    Epic epic = (Epic) task;
                    epicM.put(epic.getId(), epic);
                    prioritizedTasks.add(epic);

                } else if (task.getType().equals(Type.SUBTASK)) {
                    Subtask subtask = (Subtask) task;
                    subTaskM.put(subtask.getId(), subtask);
                    Epic epic = epicM.get(subtask.getEpicId());
                    epic.addSubtaskId(subtask.getId());
                    prioritizedTasks.add(subtask);
                }
            }

            if (counter != 1) counter++;
        } catch (IOException e) {
            throw new ManagersExep("Ошибка загрузки из файла");
        }
    }

    private static String toString(Task task) {
        String epicId = "";
        if (task.getType().equals(Type.SUBTASK)) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + epicId + ","
                + task.getStartTime() + ","
                + task.getDuration() + "\n";

    }

    private static Task stringToTask(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        Type tasktype = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        int epicId = 0;
        LocalDateTime startTime = !split[6].isBlank() ? LocalDateTime.parse(split[6]) : null;
        Duration duration = !split[7].isBlank() ? Duration.parse(split[7]) : null;
        if (tasktype.equals(Type.SUBTASK)) {
            epicId = Integer.parseInt(split[5]);
        }

        if (tasktype == Type.TASK) {
            Task task = new Task(name, description, status);
            task.setId(id);
            task.setStatus(status);
            task.setStartTime(startTime);
            task.setDuration(duration);
            return task;
        } else if (tasktype == Type.EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            epic.setStartTime(startTime);
            epic.setDuration(duration);
            return epic;
        } else {
            Subtask subtask = new Subtask(name, description, status, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            subtask.setStartTime(startTime);
            subtask.setDuration(duration);
            return subtask;

        }
    }

}
