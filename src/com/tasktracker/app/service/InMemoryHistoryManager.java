package com.tasktracker.app.service;

import com.tasktracker.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> requestHistory = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (requestHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node node = head;
        while (node != null) {
            history.add(node.getTask());
            node = node.getNext();
        }
        return history;
    }

    @Override
    public void remove(int counter) {
        Node node = requestHistory.remove(counter);
        if (node != null) {
            removeNode(node);
        }

    }
    public Node newNode (Task task){
        final Node node = new Node(tail, task, null);
        if (tail == null){
            head = node;
        }else {
            tail.next = node;
        }
        tail = node;
        return node;
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        } else {
            Node previous = node.getPrevious();
            Node next = node.getNext();
            if (node == head) {
                head = next;
            }
            if (node == tail) {
                tail = previous;
            }
            if (previous != null) {
                previous.setNext(node.getNext());
            }
            if (next != null) {
                next.setPrevious(node.getPrevious());
            }
        }

    }

    private void linkLast(Task task) {
        Node newNode = new Node(null, task, tail);
        if (tail != null) {
            tail.next = newNode;
        }
        tail = newNode;
        if (head == null) {
            head = newNode;
        }
        requestHistory.put(task.getId(), newNode);
    }


    public class Node {
        public Task task;
        public Node next;
        public Node previous;

        public Node(Node previous, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.previous = previous;
        }

        public Task getTask() {
            return task;
        }


        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task, next, previous);
        }
    }
}
