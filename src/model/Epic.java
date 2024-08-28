package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description, Status status, Type type) {
        super(name, description, status, type);
    }

    public void addSubtaskId(int subTaskId){
        subtaskIdList.add(subTaskId);
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }


}
