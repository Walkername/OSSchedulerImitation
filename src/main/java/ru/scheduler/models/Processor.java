package ru.scheduler.models;

import java.util.concurrent.atomic.AtomicInteger;

import ru.scheduler.enums.State;

public class Processor {

    private Task executionTask;
    private Thread executionThread;
    private AtomicInteger timeToFinish;

    public void executeTask(Task task, int interval) {
        executionThread = new Thread(() -> execute(task, interval));
        executionThread.setDaemon(true);
        executionThread.start();
    }

    public void execute(Task task, int interval) {
        executionTask = task;
        timeToFinish = new AtomicInteger(task.getDuration().get());
        task.setState(State.RUNNING);
        while (timeToFinish.get() != 0) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                break;
            }
            timeToFinish.decrementAndGet();
        }
        executionTask.setState(State.SUSPENDED);
    }

    public void interruptCurrentTask() {
        executionThread.interrupt();
    }

    public Task getExecutionTask() {
        return this.executionTask;
    }

    public AtomicInteger getTimeToFinish() {
        return this.timeToFinish;
    }
}
