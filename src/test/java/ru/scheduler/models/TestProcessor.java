package ru.scheduler.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

public class TestProcessor {

    @Test
    public void testSuspendedState() {
        Processor processor = new Processor();

        int duration = 4;
        //Task task = new Task("", TaskType.MAIN, State.READY, Priority.THIRD, duration);
        //processor.execute(task, 0);
//        Assertions.assertEquals(State.SUSPENDED, task.getState());
//
//        //Task task1 = new Task("", TaskType.EXTENDED, State.READY, Priority.THIRD, duration);
//        //processor.execute(task1, 0);
//        Assertions.assertEquals(State.SUSPENDED, task1.getState());
    }

    @Test
    public void testRunningStateInThread() throws InterruptedException {
        Processor processor = new Processor();

        int interval = 10;
        int duration = 4;
//        Task task = new Task("", TaskType.MAIN, State.READY, Priority.THIRD, duration);
//        processor.executeTask(task, interval);
//
//        while (task.getState() != State.RUNNING) {
//            Thread.sleep(interval);
//        }
//        Assertions.assertEquals(task, processor.getExecutionTask());
//        Assertions.assertEquals(State.RUNNING, processor.getExecutionTask().getState());
    }

    @Test
    public void testInterruptCurrentTask() throws InterruptedException {
        Processor processor = new Processor();
        Scheduler scheduler = new Scheduler(processor);

        int interval = 20;
        int taskDuration = 4;
        Task task1 = new Task("", TaskType.MAIN, State.READY, Priority.SECOND, taskDuration, false);
        Task task2 = new Task("", TaskType.EXTENDED, State.READY, Priority.THIRD, taskDuration, false);

        scheduler.getReadyTasks().get(Priority.SECOND).add(task1);
        scheduler.launchProcessorAccessor(interval);
        while (task1.getState() != State.RUNNING) {
            Thread.sleep(1);
        }
        Assertions.assertEquals(State.RUNNING, processor.getExecutionTask().getState());
        scheduler.getReadyTasks().get(Priority.THIRD).add(task2);
        while (task1.getState() != State.READY) {
            Thread.sleep(1);
        }
        Assertions.assertEquals(State.RUNNING, processor.getExecutionTask().getState());
        Assertions.assertEquals(State.READY, task1.getState());
    }

    @Test
    public void testGetProcessorTime() {
        Processor processor = new Processor();
        int taskDuration = 15;
        Task task = new Task("", TaskType.MAIN, State.READY, Priority.SECOND, 15, false);
        processor.execute(task, 10);

        int expectedDuration = 0;
        Assertions.assertEquals(expectedDuration, processor.getTimeToFinish().get());
    }
}
