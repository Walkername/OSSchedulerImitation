package ru.scheduler.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

import java.util.Queue;

public class TestOperationSystem {

    @Test
    public void testAddCustomTask() {
        OperationSystem operationSystem = new OperationSystem();

        Task task = new Task("", TaskType.EXTENDED, State.READY, Priority.FIRST, 3, false);
        operationSystem.addCustomTask(task);

        Assertions.assertTrue(operationSystem.getSchedulerReadyTasks().get(Priority.FIRST).contains(task));
    }

    @Test
    public void testGetExecutionTask() throws InterruptedException {
        OperationSystem operationSystem = new OperationSystem();

        int interval = 5;

        Task task = new Task("", TaskType.EXTENDED, State.READY, Priority.FIRST, 3, false);
        operationSystem.addCustomTask(task);
        operationSystem.launchOperationSystem(5);
        Thread.sleep(interval * 10);

        Assertions.assertFalse(operationSystem.getSchedulerFinishedTasks().isEmpty());
        Assertions.assertNull(operationSystem.getExecutionTask());
        Assertions.assertEquals(0, operationSystem.getProcessorTimer().get());
    }

    @Test
    public void testGenerateTasks() {
        OperationSystem operationSystem = new OperationSystem();

        int requestedTasksNumber = 20;

        operationSystem.generateTasks(requestedTasksNumber);

        int generatedTasksNumber = 0;
        for (Queue<Task> queue : operationSystem.getSchedulerReadyTasks().values()) {
            generatedTasksNumber += queue.size();
        }

        Assertions.assertEquals(requestedTasksNumber, generatedTasksNumber);
    }
}
