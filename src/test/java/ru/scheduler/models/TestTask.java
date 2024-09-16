package ru.scheduler.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class TestTask {

    @Test
    public void testSettersGetters() {
        Task task = new Task("", TaskType.MAIN, State.SUSPENDED, Priority.FIRST, 5, false);

        // Name setter
        task.setName("Calculator");
        Assertions.assertEquals("Calculator", task.getName());

        // Type setter
        task.setType(TaskType.EXTENDED);
        Assertions.assertEquals(TaskType.EXTENDED, task.getType());

        // State setter
        task.setState(State.RUNNING);
        Assertions.assertEquals(State.RUNNING, task.getState());

        // Priority setter
        task.setPriority(Priority.ZERO);
        Assertions.assertEquals(Priority.ZERO, task.getPriority());

        // Duration setter
        task.setDuration(new AtomicInteger(25));
        Assertions.assertEquals(25, task.getDuration().intValue());

        // Description getter
        Assertions.assertNotNull(task.getDescription());
    }

    @Test
    public void testEqualsAndHash() {
        Task task1 = new Task("", TaskType.MAIN, State.READY, Priority.SECOND, 40, false);
        Task task2 = new Task("", TaskType.MAIN, State.READY, Priority.SECOND, 40, false);

        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
        Assertions.assertEquals(task1, task2);

        task1.setState(State.SUSPENDED);
        Assertions.assertNotEquals(task2, task1);
    }

    @Test
    public void testToString() {
        Task task1 = new Task("", TaskType.MAIN, State.READY, Priority.SECOND, 40, false);
        Task task2 = new Task("", TaskType.MAIN, State.READY, Priority.SECOND, 40, false);

        Assertions.assertEquals(task1.toString(), task2.toString());

        task1 = new Task("Yfg", TaskType.MAIN, State.READY, Priority.SECOND, 40, false);
        task2 = new Task("SAgffg", TaskType.EXTENDED, State.READY, Priority.SECOND, 40, false);

        Assertions.assertNotEquals(task1.toString(), task2.toString());
    }

    @Test
    public void testAllPossibleTasks() {
        // Checking of all possible tasks
        TaskType[] types = new TaskType[TaskType.values().length * State.values().length * Priority.values().length * 5];
        State[] states = new State[TaskType.values().length * State.values().length * Priority.values().length * 5];
        Priority[] priorities = new Priority[TaskType.values().length * State.values().length * Priority.values().length * 5];
        int[] durations = new int[TaskType.values().length * State.values().length * Priority.values().length * 5];

        TaskType[] receivedTypes = new TaskType[TaskType.values().length * State.values().length * Priority.values().length * 5];
        State[] receivedStates = new State[TaskType.values().length * State.values().length * Priority.values().length * 5];
        Priority[] receivedPriorities = new Priority[TaskType.values().length * State.values().length * Priority.values().length * 5];
        int[] receivedDurations = new int[TaskType.values().length * State.values().length * Priority.values().length * 5];
        int k = 0;
        for (TaskType type : TaskType.values()) {
            for (State state : State.values()) {
                for (Priority priority : Priority.values()) {
                    for (int i = 1; i < 6; i++) {
                        types[k] = type;
                        states[k] = state;
                        priorities[k] = priority;
                        durations[k] = i;

                        Task createdTask = new Task("", type, state, priority, i, false);
                        receivedTypes[k] = createdTask.getType();
                        receivedStates[k] = createdTask.getState();
                        receivedPriorities[k] = createdTask.getPriority();
                        receivedDurations[k] = createdTask.getDuration().intValue();
                        k++;
                    }
                }
            }
        }
        Assertions.assertArrayEquals(types, receivedTypes);
        Assertions.assertArrayEquals(states, receivedStates);
        Assertions.assertArrayEquals(priorities, receivedPriorities);
        Assertions.assertArrayEquals(durations, receivedDurations);
    }
}
