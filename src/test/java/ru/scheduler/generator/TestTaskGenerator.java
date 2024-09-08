package ru.scheduler.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.scheduler.models.Processor;
import ru.scheduler.models.Scheduler;
import ru.scheduler.models.Task;

import java.util.Queue;

public class TestTaskGenerator {

    @Test
    public void testGenerateTasksNumber() {
        Processor processor = new Processor();
        Scheduler scheduler = new Scheduler(processor);
        TaskGenerator taskGenerator = new TaskGenerator(scheduler);

        int requestedTasksNumber = 10;
        taskGenerator.generateOnce(requestedTasksNumber);
        int generatedTasksNumber = 0;
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);

        requestedTasksNumber = 20;
        taskGenerator.generateOnce(requestedTasksNumber);
        generatedTasksNumber = 0;
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);

        requestedTasksNumber = 30;
        taskGenerator.generateOnce(requestedTasksNumber);
        generatedTasksNumber = 0;
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);

        requestedTasksNumber = 50;
        taskGenerator.generateOnce(requestedTasksNumber);
        generatedTasksNumber = 0;
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);

        requestedTasksNumber = 100;
        taskGenerator.generateOnce(requestedTasksNumber);
        generatedTasksNumber = 0;
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);
    }

    @Test
    public void testGenerateTasksNumberInThread() throws InterruptedException {
        Processor processor = new Processor();
        Scheduler scheduler = new Scheduler(processor);
        TaskGenerator taskGenerator = new TaskGenerator(scheduler);

        int interval = 1;

        int requestedTasksNumber = 10;
        taskGenerator.generate(requestedTasksNumber, 0);
        int generatedTasksNumber = 0;
        Thread.sleep(interval * requestedTasksNumber * 2);
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);

        requestedTasksNumber = 100;
        taskGenerator.generate(requestedTasksNumber, 0);
        generatedTasksNumber = 0;
        Thread.sleep(interval * requestedTasksNumber * 2);
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);

        requestedTasksNumber = 1000;
        taskGenerator.generate(requestedTasksNumber, 0);
        generatedTasksNumber = 0;
        Thread.sleep(interval * requestedTasksNumber * 2);
        for (Queue<Task> queue : scheduler.getReadyTasks().values()) {
            generatedTasksNumber += queue.size();
            queue.clear();
        }
        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);
    }
}
