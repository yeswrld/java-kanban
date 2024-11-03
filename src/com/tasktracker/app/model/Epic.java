package com.tasktracker.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.endTime = null;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addSubtaskId(int subTaskId) {
        subtaskIdList.add(subTaskId);
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void deleteEpicSubtask(Integer idSubtask) {
        subtaskIdList.remove(idSubtask);
    }

    public void clearSubtaskMapIdList() {
        subtaskIdList.clear();
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subtaskIdList, epic.subtaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList);
    }

}
