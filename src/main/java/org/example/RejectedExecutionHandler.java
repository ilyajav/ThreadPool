package org.example;

public class RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, CustomThreadPool executor) {
        System.out.println("[Rejected] Task <" + r + "> was rejected due to overload!");
    }
}