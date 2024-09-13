package com.tasktracker.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    final private List<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description ) {
        super(name, description, Status.NEW);
    }

    public void addSubtaskId(int subTaskId) {
        subtaskIdList.add(subTaskId);
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void deleteEpicSubtask(Integer idSubtask){
        subtaskIdList.remove(idSubtask);
    }

    public void clearSubtaskMapIdList (){
        subtaskIdList.clear();
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }
}
