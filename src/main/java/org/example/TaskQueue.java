package org.example;

import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue extends LinkedBlockingQueue<Runnable> {
    private final String name;

    public TaskQueue(int capacity, String name) {
        super(capacity);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}