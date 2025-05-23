package org.example;

import java.util.concurrent.Executor;

public interface CustomExecutor extends Executor {
    void execute(Runnable command);
}