package ru.scheduler.models;

import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

import java.util.*;

public class Scheduler {

    //private final Queue<Task> newTasks = new ConcurrentLinkedQueue<>();

    private final Processor processor;
    private final Queue<Task> finishedTasks = new LinkedList<>();
    private final Map<Priority, Queue<Task>> readyTasks = new HashMap<>() {{
        put(Priority.ZERO, new LinkedList<>());
        put(Priority.FIRST, new LinkedList<>());
        put(Priority.SECOND, new LinkedList<>());
        put(Priority.THIRD, new LinkedList<>());
    }};
    private final Map<Priority, Queue<Task>> waitingTasks = new HashMap<>() {{
        put(Priority.ZERO, new LinkedList<>());
        put(Priority.FIRST, new LinkedList<>());
        put(Priority.SECOND, new LinkedList<>());
        put(Priority.THIRD, new LinkedList<>());
    }};
    private boolean isAllTasksExecuted = false;

    public Scheduler(Processor processor) {
        this.processor = processor;
    }

    public void launchScheduleDaemon(int interval) {
        //launchTaskMapper(interval);
        launchProcessorAccessor(interval);
    }

//    private void launchTaskMapper(int interval) {
//        Thread thread = new Thread(() -> {
//            int newTasksCounter = 0;
//            while (!isAllTasksExecuted) {
//                if (newTasksCounter < newTasks.size()) {
//                    while (!newTasks.isEmpty()) {
//                        Task newTask = newTasks.poll();
//                        newTask.setState(State.READY);
//                        readyTasks.get(newTask.getPriority()).add(newTask);
//                    }
//                }
//                try {
//                    Thread.sleep(interval);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        thread.start();
//    }

    private void launchProcessorAccessor(int interval) {
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
                    printProcessingState("Задача " + toExecute + " начала выполняться", interval);
                    //System.out.println("Задача " + toExecute + " начала выполняться");
                } else if (currentTask.getState() == State.SUSPENDED) {
                    finishedTasks.add(processor.getExecutionTask());
                    removeTaskFromQueues(toExecute);
                    processor.executeTask(toExecute, interval);
                    printProcessingState("Задача " + toExecute + " начала выполняться", interval);
                    //System.out.println("Задача " + toExecute + " начала выполняться");
                } else if (currentTask.getState() == State.RUNNING) {
                    if (toExecute.getPriority().ordinal() > currentTask.getPriority().ordinal()) {
                        processor.interruptCurrentTask(interval);
                        removeTaskFromQueues(toExecute);
                        processor.executeTask(toExecute, interval);
                        printProcessingState("Задача " + toExecute + " заменила " + currentTask, interval);
                        //System.out.println("Задача " + toExecute + " заменила " + currentTask);
                        if (currentTask.getType() == TaskType.EXTENDED) {
                            currentTask.setState(State.WAITING);
                            waitingTasks.get(currentTask.getPriority()).add(currentTask);
                            printProcessingState(currentTask + " теперь WAITING", interval);
                            //System.out.println(currentTask + " теперь WAITING");
                        } else {
                            currentTask.setState(State.READY);
                            readyTasks.get(currentTask.getPriority()).add(currentTask);
                            //newTasks.add(currentTask);
                            printProcessingState(currentTask + " теперь SUSPENDED", interval);
                            //System.out.println(currentTask + " теперь SUSPENDED");
                        }
                    } else {
                        printProcessingState("Задача " + toExecute + " пока не может быть выполнена", interval);
                        //System.out.println("Задача " + toExecute + " пока не может быть выполнена");
                    }
                }
            }
            isAllTasksExecuted = true;
        });
        thread.setDaemon(true);
        thread.start();
    }

    private Task decideWhichTaskWillExecuted() {
        Task maxPriorityTask = null;
        for (Priority priority : Priority.values()) {
            Task readyTaskForCurPriority = readyTasks.get(priority).peek();
            Task waitingTaskForCurPriority = waitingTasks.get(priority).peek();
            if (readyTaskForCurPriority != null) {
                maxPriorityTask = Objects.requireNonNullElse(waitingTaskForCurPriority, readyTaskForCurPriority);
            } else {
                maxPriorityTask = waitingTaskForCurPriority != null ? waitingTaskForCurPriority : maxPriorityTask;
            }
        }
        return maxPriorityTask;
    }

    private void removeTaskFromQueues(Task task) {
        if (task.getState() == State.READY) {
            readyTasks.get(task.getPriority()).remove();
        } else if (task.getState() == State.WAITING) {
            waitingTasks.get(task.getPriority()).remove();
        } else {
            throw new RuntimeException();
        }
    }

    private void printProcessingState(String message, int interval) {
        if (interval >= 1000) {
            System.out.println(message);
        }
    }

    public Map<Priority, Queue<Task>> getReadyTasks() { return readyTasks; }

    public Queue<Task> getFinishedTasks() { return finishedTasks; }

//    public Queue<Task> getNewTasks() {
//        return newTasks;
//    }

    public Map<Priority, Queue<Task>> getWaitingTasks() { return waitingTasks; }
}
