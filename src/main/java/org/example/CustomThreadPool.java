package org.example;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPool implements CustomExecutor {
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final int queueSize;
    private final int minSpareThreads;

    private final List<TaskQueue> queues = new CopyOnWriteArrayList<>();
    private final Set<Worker> workers = new CopyOnWriteArraySet<>();
    private final AtomicInteger workerId = new AtomicInteger(0);

    private final boolean shutdown = false;

    private final RejectedExecutionHandler rejectedHandler;

    public CustomThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime,
                            TimeUnit timeUnit, int queueSize, int minSpareThreads) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.queueSize = queueSize;
        this.minSpareThreads = minSpareThreads;
        this.rejectedHandler = new RejectedExecutionHandler();

        for (int i = 0; i < corePoolSize; i++) {
            addWorker();
        }
    }

    public void addWorker() {
        if (shutdown) return;
        if (workers.size() >= maxPoolSize) return;

        String name = "MyPool-worker-" + workerId.incrementAndGet();
        System.out.println("[ThreadFactory] Creating new thread: " + name);

        TaskQueue taskQueue = new TaskQueue(queueSize, name);
        queues.add(taskQueue);

        Worker worker = new Worker(name, taskQueue, this);
        workers.add(worker);
        worker.start();
    }

    private int roundRobinIndex = 0;

    @Override
    public void execute(Runnable command) {
        if (shutdown) {
            System.out.println("[Rejected] Task was rejected because pool is shutting down.");
            return;
        }

        if (queues.isEmpty()) {
            rejectedHandler.rejectedExecution(command, this);
            return;
        }

        TaskQueue targetQueue = queues.get(roundRobinIndex % queues.size());
        roundRobinIndex++;

        if (!targetQueue.offer(command)) {
            rejectedHandler.rejectedExecution(command, this);
        } else {
            System.out.println("[Pool] Task accepted into queue #" + targetQueue.getName() + ": " + command);
        }
    }

    public void removeWorker(Worker worker) {
        workers.remove(worker);
        queues.removeIf(q -> q.getName().equals(worker.getName()));

        while (workers.size() < minSpareThreads) {
            addWorker();
        }
    }

    public long getKeepAliveTimeNanos() {
        return timeUnit.toNanos(keepAliveTime);
    }

    public int getPoolSize() {
        return workers.size();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public boolean isShutdown() {
        return shutdown;
    }
}