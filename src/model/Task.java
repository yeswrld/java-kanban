package model;

public class Task {
    private String name;
    private String description;
    private Status status;
    private int id;
    private Type type;

    public Task(String name, String description, Status status, Type type) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
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
        return "Наименование = '" + name +
                ", description = '" + description + '\'' +
                ", status = " + status +
                ", id = " + id +
                ", type = " + type;
    }
}
