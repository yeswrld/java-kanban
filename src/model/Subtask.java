package model;

public class Subtask extends Task {
     private int epicId ;

    public Subtask(String name, String description, Status status, Type type, int epicId) {
        super(name, description, status, type);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
