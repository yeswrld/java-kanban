package com.tasktracker.app.service;

import com.tasktracker.app.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

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
    public void updateSubstask(Subtask subtask) {
        super.updateSubstask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
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
        String head = "id,type,name,status,description,epic,\n";
        try {
            Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(head);
            for (Task task : getTasks()) {
                bufferedWriter.write(toString(task));
            }
            for (Task task : getEpics()) {
                bufferedWriter.write(toString(task));
            }
            for (Task task : getSubtasks()) {
                bufferedWriter.write(toString(task));
            }
            bufferedWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load(File file) {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isEmpty()) continue;
                Task task = stringToTask(line);
                if (counter < task.getId()) {
                    counter = task.getId();
                }
                if (task.getClass().equals(Task.class)) {
                    taskM.put(task.getId(), task);
                } else if (task.getClass().equals(Epic.class)) {
                    Epic epic = (Epic) task;
                    epicM.put(epic.getId(), epic);
                } else if (task.getClass().equals(Subtask.class)) {
                    Subtask subtask = (Subtask) task;
                    subTaskM.put(subtask.getId(), subtask);
                    Epic epic = epicM.get(subtask.getEpicId());
                    epic.addSubtaskId(subtask.getId());
                }
            }

            if (counter != 1) counter++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toString(Task task) {
        String epicId = "";
        if (task.getType().equals(Type.SUBTASK)) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId + "\n";

    }

    private static Task stringToTask(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        Type tasktype = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        int epicId = 0;
        if (tasktype.equals(Type.SUBTASK)) {
            epicId = Integer.parseInt(split[5]);
        }

        if (tasktype == Type.TASK) {
            Task task = new Task(name, description, status);
            task.setId(id);
            task.setStatus(status);
            return task;
        } else if (tasktype == Type.EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else {
            Subtask subtask = new Subtask(name, description, status, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;

        }
    }

}
