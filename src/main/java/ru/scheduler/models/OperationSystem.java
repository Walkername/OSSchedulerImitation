package ru.scheduler.models;

import ru.scheduler.generator.TaskGenerator;
import ru.scheduler.enums.Priority;

import java.util.Map;
import java.util.Queue;

public class OperationSystem {

    Processor processor = new Processor();
    Scheduler scheduler = new Scheduler(processor);
    TaskGenerator taskGenerator = new TaskGenerator(scheduler);

    public void launchOperationSystem() {
        scheduler.launchScheduleDaemon(1000);
        taskGenerator.generate(0, 3000);
    }

    public void generateTasks(int number) {
        taskGenerator.generateOnce(number);
    }

//    public Queue<Task> getSchedulerNewTasks() {
//        return scheduler.getNewTasks();
//    }

    public Map<Priority, Queue<Task>> getSchedulerReadyTasks() {
        return scheduler.getReadyTasks();
    }

    public Map<Priority, Queue<Task>> getSchedulerWaitingTasks() {
        return scheduler.getWaitingTasks();
    }

    public Queue<Task> getSchedulerFinishedTasks() {
        return scheduler.getFinishedTasks();
    }

    public Task getExecutionTask() {
        return processor.getExecutionTask();
    }

    public void addCustomTask(Task task) {
        scheduler.getReadyTasks().get(task.getPriority()).add(task);
    }
}
