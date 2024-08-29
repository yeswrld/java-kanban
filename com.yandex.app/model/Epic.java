package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description ) {
        super(name, description, Status.NEW);
    }

    public void addSubtaskId(int subTaskId) {
        subtaskIdList.add(subTaskId);
    }

    public ArrayList<Integer> getSubtaskIdList() {
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
