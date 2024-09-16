package ru.scheduler.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

public class TestScheduler {

    @Test
    public void testWaitingTasks() throws InterruptedException {
        Processor processor = new Processor();
        Scheduler scheduler = new Scheduler(processor);

        int interval = 50;
        int taskDuration = 4;
        Task task1 = new Task("", TaskType.EXTENDED, State.READY, Priority.SECOND, taskDuration, false);
        Task task2 = new Task("", TaskType.EXTENDED, State.READY, Priority.THIRD, taskDuration, false);

        scheduler.getReadyTasks().get(task1.getPriority()).add(task1);
        scheduler.launchProcessorAccessor(interval);
        while (task1.getState() != State.RUNNING) {
            Thread.sleep(1);
        }
        Assertions.assertEquals(State.RUNNING, task1.getState());
        scheduler.getReadyTasks().get(task2.getPriority()).add(task2);
        while (task1.getState() != State.WAITING) {
            Thread.sleep(1);
        }
        // Task2
        Assertions.assertEquals(State.RUNNING, task2.getState());
        //Task1
        //Assertions.assertEquals(State.WAITING, scheduler.getWaitingTask().get(Priority.SECOND).peek().getState());

        while (task2.getState() != State.SUSPENDED) {
            Thread.sleep(1);
        }
        while (task1.getState() != State.RUNNING) {
            Thread.sleep(1);
        }
        Assertions.assertEquals(State.RUNNING, task1.getState());
    }

    // Test in order to get suspended tasks
    @Test
    public void testFinishedTasks() throws InterruptedException {
        Processor processor = new Processor();
        Scheduler scheduler = new Scheduler(processor);
        int interval = 5;

        Task task1 = new Task("task1", TaskType.MAIN, State.READY, Priority.FIRST, 1, false);
        scheduler.getReadyTasks().get(task1.getPriority()).add(task1);
        Task task2 = new Task("task2", TaskType.EXTENDED, State.READY, Priority.ZERO, 1, false);
        scheduler.getReadyTasks().get(task2.getPriority()).add(task2);
        Task task3 = new Task("task3", TaskType.MAIN, State.READY, Priority.THIRD, 1, false);
        scheduler.getReadyTasks().get(task3.getPriority()).add(task3);
        Task task4 = new Task("task4", TaskType.MAIN, State.READY, Priority.FIRST, 1, false);
        scheduler.getReadyTasks().get(task4.getPriority()).add(task4);
        Task task5 = new Task("task5", TaskType.MAIN, State.READY, Priority.SECOND, 1, false);
        scheduler.getReadyTasks().get(task5.getPriority()).add(task5);
        Task task6 = new Task("task6", TaskType.EXTENDED, State.READY, Priority.SECOND, 1, false);
        scheduler.getReadyTasks().get(task6.getPriority()).add(task6);
        Task task7 = new Task("task7", TaskType.MAIN, State.READY, Priority.THIRD, 1, false);
        scheduler.getReadyTasks().get(task7.getPriority()).add(task7);
        Task task8 = new Task("task8", TaskType.MAIN, State.READY, Priority.ZERO, 1, false);
        scheduler.getReadyTasks().get(task8.getPriority()).add(task8);
        Task task9 = new Task("task9", TaskType.EXTENDED, State.READY, Priority.ZERO, 1, false);
        scheduler.getReadyTasks().get(task9.getPriority()).add(task9);
        Task task10 = new Task("task10", TaskType.EXTENDED, State.READY, Priority.SECOND, 1, false);
        scheduler.getReadyTasks().get(task10.getPriority()).add(task10);

        scheduler.launchProcessorAccessor(interval);
        while (scheduler.getFinishedTasks().size() != 10) {
            Thread.sleep(5);
        }
        System.out.println(scheduler.getFinishedTasks());

        Assertions.assertEquals(task3, scheduler.getFinishedTasks().poll());
        Assertions.assertEquals(task7, scheduler.getFinishedTasks().poll());

        Assertions.assertEquals(task5, scheduler.getFinishedTasks().poll());
        Assertions.assertEquals(task6, scheduler.getFinishedTasks().poll());
        Assertions.assertEquals(task10, scheduler.getFinishedTasks().poll());

        Assertions.assertEquals(task1, scheduler.getFinishedTasks().poll());
        Assertions.assertEquals(task4, scheduler.getFinishedTasks().poll());
        Assertions.assertEquals(task2, scheduler.getFinishedTasks().poll());

        Assertions.assertEquals(task8, scheduler.getFinishedTasks().poll());
        Assertions.assertEquals(task9, scheduler.getFinishedTasks().poll());

        Assertions.assertEquals(State.SUSPENDED, task3.getState());
        Assertions.assertEquals(State.SUSPENDED, task7.getState());

        Assertions.assertEquals(State.SUSPENDED, task5.getState());
        Assertions.assertEquals(State.SUSPENDED, task6.getState());
        Assertions.assertEquals(State.SUSPENDED, task10.getState());

        Assertions.assertEquals(State.SUSPENDED, task1.getState());
        Assertions.assertEquals(State.SUSPENDED, task4.getState());
        Assertions.assertEquals(State.SUSPENDED, task2.getState());

        Assertions.assertEquals(State.SUSPENDED, task8.getState());
        Assertions.assertEquals(State.SUSPENDED, task9.getState());
    }
}
