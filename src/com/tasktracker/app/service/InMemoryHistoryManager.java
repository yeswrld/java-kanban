package com.tasktracker.app.service;

import com.tasktracker.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();

    private Node first;
    private Node last;


    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    public void remove(int id) {
        removeNode(history.remove(id));
    }


    private void linkLast(Task task) {
        Node newNode = new Node(last, task, null);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node prev = node.previous;
        Node next = node.next;
        if (node == first && last == node) {
            first = null;
            last = null;
        } else if (first == node) {
            first = next;
            first.previous = null;
        } else if (last == node) {
            last = prev;
            last.next = null;
        } else {
            prev.next = next;
            next.previous = prev;
        }
    }


    @Override
    public List<Task> getTasks() {
        List<Task> historyList = new LinkedList<>();
        Node node = first;
        while (node != null) {
            historyList.add(node.task);
            node = node.next;
        }
        return historyList;
    }

    private static class Node {
        public Task task;
        public Node next;
        public Node previous;

        public Node(Node previous, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.previous = previous;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task, next, previous);
        }
    }
}