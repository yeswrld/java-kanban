package com.tasktracker.app.service;

import com.tasktracker.app.service.CustomExeptions.ManagersExep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFile() {
        Path path = Paths.get("src/resources/storage.csv");
        File file = new File(path.toUri());
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagersExep("Ошибка при создании файла");
            }
        }
        return FileBackedTaskManager.load(getDefaultHistory(), file);
    }

    public static TaskManager getMemoryManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }
}
