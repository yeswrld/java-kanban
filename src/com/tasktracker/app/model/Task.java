package com.tasktracker.app.model;

public class Task {
    private String name;
    private String description;
    private Status status;
    private int id;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return   "name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", status = " + status +
                ", id = " + id;
    }
}
