package ru.scheduler.models;

import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

import java.util.*;

public class Scheduler {

    private final Processor processor;

    private final Queue<Task> finishedTasks = new LinkedList<>();

    private final Map<Priority, Queue<Task>> readyTasks = new HashMap<>() {{
        put(Priority.ZERO, new LinkedList<>());
        put(Priority.FIRST, new LinkedList<>());
        put(Priority.SECOND, new LinkedList<>());
        put(Priority.THIRD, new LinkedList<>());
    }};

    private Task waitingTask = null;

//    private final Map<Priority, Queue<Task>> waitingTasks = new HashMap<>() {{
//        put(Priority.ZERO, new LinkedList<>());
//        put(Priority.FIRST, new LinkedList<>());
//        put(Priority.SECOND, new LinkedList<>());
//        put(Priority.THIRD, new LinkedList<>());
//    }};

    public Scheduler(Processor processor) {
        this.processor = processor;
    }

    private void releaseWaitingTask(Task currentTask) {
        if (currentTask == null || !currentTask.isSystem()) {
            if (waitingTask != null) {
                Task task = waitingTask;
                readyTasks.get(task.getPriority()).add(task);
                waitingTask = null;
            }
        }
    }

    public void launchProcessorAccessor(int interval) {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Task currentTask = processor.getExecutionTask();
                Task toExecute = decideWhichTaskWillExecuted();

                releaseWaitingTask(currentTask);

                if (toExecute == null) {
                    if (currentTask != null && currentTask.getState() == State.SUSPENDED) {
                        finishedTasks.add(processor.getExecutionTask());
                        processor.setExecutionTask(null);
                        //break;
                    }
                    continue;
                }

                if (currentTask == null) {
                    removeTaskFromQueues(toExecute); // remove task from ready queues
                    processor.executeTask(toExecute, interval);
                } else if (currentTask.getState() == State.SUSPENDED) {
                    addToFinishedTasks(currentTask, toExecute, interval);
                }
                // It's moment where scheduler can change execution task in processor
                else if (currentTask.getState() == State.RUNNING) {

                    if (currentTask.getType() == TaskType.EXTENDED
                            && !currentTask.isSystem()
                            && toExecute.isSystem()
                    ) {
                        addToWaitingTasks(currentTask, toExecute, interval);

                    } else if (toExecute.getPriority().ordinal() > currentTask.getPriority().ordinal()) {
                        addToReadyTasks(currentTask, toExecute, interval);
                    }

                }

            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void addToFinishedTasks(Task currentTask, Task toExecute, int interval) {
        try {
            finishedTasks.add(currentTask);

            removeTaskFromQueues(toExecute);
            processor.executeTask(toExecute, interval);
        } catch (NullPointerException ignored) {
        }
    }

    private void addToWaitingTasks(Task currentTask, Task toExecute, int interval) {
        try {
            processor.interruptCurrentTask();
            currentTask.setState(State.WAITING);
            waitingTask = currentTask;

            removeTaskFromQueues(toExecute);
            processor.executeTask(toExecute, interval);
        } catch (NullPointerException ignored) {
        }
    }

    private void addToReadyTasks(Task currentTask, Task toExecute, int interval) {
        try {
            processor.interruptCurrentTask();
            removeTaskFromQueues(toExecute);
            processor.executeTask(toExecute, interval);
            currentTask.setState(State.READY);
            readyTasks.get(currentTask.getPriority()).add(currentTask);
        } catch (NullPointerException ignored) {

        }
    }

    private Task decideWhichTaskWillExecuted() {
        Task maxPriorityTask = null;
        for (Priority priority : Priority.values()) {
            Task readyTask = readyTasks.get(priority).peek();
            if (readyTask != null) {
                maxPriorityTask = readyTask;
            }
        }
        return maxPriorityTask;
    }

    private void removeTaskFromQueues(Task task) {
        try {
            readyTasks.get(task.getPriority()).remove();
        } catch (NullPointerException ignored) {

        }
    }

    public Map<Priority, Queue<Task>> getReadyTasks() {
        return readyTasks;
    }

    public Queue<Task> getFinishedTasks() {
        return finishedTasks;
    }

    public Task getWaitingTask() {
        return waitingTask;
    }
}
