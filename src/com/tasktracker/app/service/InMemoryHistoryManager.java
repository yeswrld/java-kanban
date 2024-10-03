package com.tasktracker.app.service;

import com.tasktracker.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();

    private Node first;
    private Node last;


    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    public Node newNode(Task task) {
        final Node node = new Node(last, task, null);
        if (last == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        return node;
    }

    private void linkLast(Task task) {
        Node newNode = new Node(null, task, last);
        if (first == null) {
            last = newNode;
            first = newNode;
        } else {
            newNode.setPrevious(last);
            last.setNext(newNode);
            last = newNode;
        }
        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        Node prev = node.getPrevious();
        Node next = node.getNext();
        if (node == first && last == node) {
            first = null;
            last = null;
        } else if (first == node && last != node) {
            first = next;
            first.setPrevious(null);
        } else if (first != node && last == node) {
            last = prev;
            last.setNext(null);
        } else {
            prev.setNext(next);
            next.setPrevious(prev);
        }
    }


    @Override
    public List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node node = first;
        while (node != null) {
            historyList.add(node.getTask());
            node = node.getNext();
        }
        return historyList;
    }

    public class Node {
        public Task task;
        public Node next;
        public Node previous;

        public Node(Node previous, Task task, Node next) {
            this.task = task;
            this.next = null;
            this.previous = null;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Task getTask() {
            return task;
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