package ru.scheduler.models;

import java.util.concurrent.atomic.AtomicInteger;

import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

public class Processor {

    private Task executionTask;
    private Thread executionThread;

    public void executeTask(Task task, int interval) {


        executionTask = task;
        AtomicInteger initialDuration = new AtomicInteger(task.getDuration().get());
        AtomicInteger timeToFinish = new AtomicInteger(task.getDuration().get());
        task.setState(State.RUNNING);
        executionThread = new Thread(() -> {
            while (timeToFinish.get() != 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    break;
                }
                if (task.getType() == TaskType.EXTENDED) {
                    task.getDuration().decrementAndGet();
                }
                timeToFinish.decrementAndGet();
            }
            executionTask.setState(State.SUSPENDED);
            executionTask.setDuration(initialDuration);
            printProcessingState("Задача " + executionTask + " выполнена", interval);
            //System.out.println("Задача " + executionTask + " выполнена");
        });
        executionThread.setDaemon(true);
        executionThread.start();
    }

    public void interruptCurrentTask(int interval) {
        executionThread.interrupt();
        printProcessingState("Задача " + executionTask + " прервана", interval);
        //System.out.println("Задача " + executionTask + " прервана");
    }

    private void printProcessingState(String message, int interval) {
        if (interval >= 1000) {
            System.out.println(message);
        }
    }

    public Task getExecutionTask() {
        return executionTask;
    }

    public void setExecutionTask(Task executionTask) {
        this.executionTask = executionTask;
    }

}
