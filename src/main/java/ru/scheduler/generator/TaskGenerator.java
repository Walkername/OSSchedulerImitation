package ru.scheduler.generator;

import ru.scheduler.models.Scheduler;
import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;
import ru.scheduler.models.Task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TaskGenerator {

    private final Scheduler scheduler;
    private final Random random = new Random();
    private final int MAX_TASK_DURATION = 6;

    private final Set<String> taskTitles = new HashSet<>(
            Arrays.asList(
                    "Calc", "The Witcher", "SCII", "Intellij IDEA",
                    "Battle.net", "Steam", "Epic Games", "WordPad",
                    "Nioh", "Dark Souls", "Chrome", "Yandex",
                    "Telegram", "Discord", "Photoshop", "Adobe Premier",
                    "PostgreSQL", "Notion", "Obsidian", "Git", "FarCry",
                    "Diablo", "Minecraft", "Overwatch", "Warcraft",
                    "Half-Life", "Quake", "Doom", "BioShock",
                    "Zelda", "God of War", "Fortnite", "Office",
                    "sys32", "java", "MATLAB", "Maple", "JAWS",
                    "Visual Studio", "python", "gcc", "g++",
                    "C#", "Javascript", "Typescript", "Bonjour",
                    "jtagserver", "Lightshot", "Alan Wake", "Starfield",
                    "Fallout", "Skyrim", "Baldur's Gate", "Cyberpunk",
                    "Lies of P", "Dark Souls", "Elden Ring", "Sekiro",
                    "Halo", "Dying Light", "Bloodborne", "Undertale"
            )
    );

    public TaskGenerator(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void generateOnce(int tasksNumber) {
        for (int i = 0; i < tasksNumber; i++) {
            Task task = generateTask();
            scheduler.getReadyTasks().get(task.getPriority()).add(task);
        }
    }

    public void generate(int tasksNumber, int interval) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < tasksNumber; i++) {
                Task task = generateTask();
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

    private Task generateTask() {
        String name = getRandomArrayElement(taskTitles.toArray(new String[0]));
        TaskType type = getRandomArrayElement(TaskType.values());
        Priority priority = getRandomArrayElement(Priority.values());
        int duration = random.nextInt(3, MAX_TASK_DURATION);
        boolean system = random.nextBoolean();

        return new Task(name, type, State.READY, priority, duration, system);
    }

    private <T> T getRandomArrayElement(T[] array) {
        int index = random.nextInt(array.length);
        return array[index];
    }
}
