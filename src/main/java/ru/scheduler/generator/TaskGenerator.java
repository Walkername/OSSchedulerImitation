package ru.scheduler.generator;

import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;
import ru.scheduler.models.Scheduler;
import ru.scheduler.models.Task;

import java.util.Random;

public class TaskGenerator {

    private final Scheduler scheduler;

    private final Random random = new Random();
    private final int MAX_TASK_DURATION = 5;

    public TaskGenerator(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void generateOnce(int tasksNumber) {
        for (int i = 0; i < tasksNumber; i++) {
            Task task = generateTask(tasksNumber);
            scheduler.getReadyTasks().get(task.getPriority()).add(task);
        }
    }

    public void generate(int tasksNumber, int interval) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < tasksNumber; i++) {
                Task task = generateTask(tasksNumber);
                scheduler.getReadyTasks().get(task.getPriority()).add(task);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private Task generateTask(int tasksNumber) {
        String name = String.valueOf(random.nextInt(tasksNumber));
        TaskType type = getRandomArrayElement(TaskType.values());
        Priority priority = getRandomArrayElement(Priority.values());
        int duration = random.nextInt(1, MAX_TASK_DURATION);

        return new Task(name, type, State.READY, priority, duration);
    }

    private <T> T getRandomArrayElement(T[] array) {
        int index = random.nextInt(array.length);
        return array[index];
    }
}
