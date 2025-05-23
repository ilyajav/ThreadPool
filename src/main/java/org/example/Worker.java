package org.example;

import java.util.concurrent.TimeUnit;

public class Worker extends Thread {
    private final TaskQueue taskQueue;
    private final CustomThreadPool pool;
    private boolean isIdle = false;

    public Worker(String name, TaskQueue taskQueue, CustomThreadPool pool) {
        super(name);
        this.taskQueue = taskQueue;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted() && !pool.isShutdown()) {
                Runnable task = taskQueue.poll(pool.getKeepAliveTimeNanos(), TimeUnit.NANOSECONDS);
                if (task != null) {
                    System.out.println("[" + getName() + "] executes: " + task);
                    task.run();
                } else {
                    if (pool.getPoolSize() > pool.getCorePoolSize()) {
                        break;
                    }
                    if (!isIdle) {
                        System.out.println("[" + getName() + "] idle timeout, stopping.");
                        isIdle = true;
                    }
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            pool.removeWorker(this);
            System.out.println("[Worker] " + getName() + " terminated.");
        }
    }
}