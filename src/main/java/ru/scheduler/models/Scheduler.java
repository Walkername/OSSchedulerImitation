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

    private Task waitingTask;

//    private final Map<Priority, Queue<Task>> waitingTasks = new HashMap<>() {{
//        put(Priority.ZERO, new LinkedList<>());
//        put(Priority.FIRST, new LinkedList<>());
//        put(Priority.SECOND, new LinkedList<>());
//        put(Priority.THIRD, new LinkedList<>());
//    }};

    public Scheduler(Processor processor) {
        this.processor = processor;
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

                if (toExecute == null) {
                    finishedTasks.add(processor.getExecutionTask());
                    break;
                }

                if (currentTask == null) {
                    removeTaskFromQueues(toExecute);
                    processor.executeTask(toExecute, interval);
                } else if (currentTask.getState() == State.SUSPENDED) {
                    addToFinishedTasks(currentTask, toExecute, interval);

                }
                // It's moment where scheduler can change execution task in processor
                else if (currentTask.getState() == State.RUNNING) {

                    // If current task is EXTENDED
                    // then scheduler can move this task to WAITING tasks in some case
                    if (currentTask.getType() == TaskType.EXTENDED) {
                        // If toExecute task is system and extended current is not
                        // then scheduler have to start toExecute
                        // and currentTask will be moved to WAITING STATE
                        // When toExecute will be executed, currentTask will be moved in READY state
                        if (toExecute.isSystem() && !currentTask.isSystem()) {
                            addToWaitingTasks(currentTask, toExecute, interval);
                        }
                        // if toExecute and current are both system
                        // then scheduler consider only priority
                        else if (toExecute.getPriority().ordinal() > currentTask.getPriority().ordinal()) {
                            addToReadyTasks(currentTask, toExecute, interval);
                        }
                    }
                    // If current task is MAIN
                    else if (toExecute.getPriority().ordinal() > currentTask.getPriority().ordinal()) {
                        addToReadyTasks(currentTask, toExecute, interval);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void addToFinishedTasks(Task currentTask, Task toExecute, int interval) {
        finishedTasks.add(currentTask);

        Task task = waitingTask;
        if (waitingTask != null) {
            task.setState(State.READY);
            readyTasks.get(task.getPriority()).add(task);
            waitingTask = null;
        }

        removeTaskFromQueues(toExecute);
        processor.executeTask(toExecute, interval);
    }

    private void addToWaitingTasks(Task currentTask, Task toExecute, int interval) {
        processor.interruptCurrentTask();
        removeTaskFromQueues(toExecute);
        processor.executeTask(toExecute, interval);
        currentTask.setState(State.WAITING);
        //waitingTasks.get(currentTask.getPriority()).add(currentTask);
        waitingTask = currentTask;
    }

    private void addToReadyTasks(Task currentTask, Task toExecute, int interval) {
        processor.interruptCurrentTask();
        removeTaskFromQueues(toExecute);
        processor.executeTask(toExecute, interval);
        currentTask.setState(State.READY);
        readyTasks.get(currentTask.getPriority()).add(currentTask);
    }

    private Task decideWhichTaskWillExecuted() {
        Task maxPriorityTask = null;
        for (Priority priority : Priority.values()) {
            for (Task task : readyTasks.get(priority)) {
                if (task.isSystem()) {
                    maxPriorityTask = task;
                    return maxPriorityTask;
                }
                maxPriorityTask = task;
            }
        }
        return maxPriorityTask;
    }

    private void removeTaskFromQueues(Task task) {
        if (task.getState() == State.READY) {
            readyTasks.get(task.getPriority()).remove();
        } else {
            throw new RuntimeException();
        }
    }

    public Map<Priority, Queue<Task>> getReadyTasks() { return readyTasks; }

    public Queue<Task> getFinishedTasks() { return finishedTasks; }

    public Task getWaitingTask() { return waitingTask; }
}
