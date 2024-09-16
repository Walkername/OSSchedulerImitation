package ru.scheduler.models;

import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {

    private String name;

    private TaskType type;

    private State state;

    // If scheduler gets task with higher priority
    // then current task (executioning) will get state READY
    // and will be moved in READY queues
    // Task with higher priority will get state RUNNING and will be moved in Processor
    private Priority priority;

    private AtomicInteger duration;

    // If scheduler gets "system" task
    // and current task "no system" will get state WAITING
    // It means that "no system" task will wait the moment when
    // "system" task will be executed
    // After that "no system" task will get state READY and will be moved in READY queues
    private boolean system;

    public Task(String name, TaskType type, State state, Priority priority, int duration, boolean system) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.priority = priority;
        this.duration = new AtomicInteger(duration);
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public AtomicInteger getDuration() {
        return duration;
    }

    public void setDuration(final AtomicInteger duration) {
        this.duration = duration;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", priority=" + priority +
                ", duration=" + duration +
                ", system=" + system +
                '}';
    }

    public String getDescription() {
        return this.name + "\n"
                + this.type + "\n"
                + this.state + "\n"
                + this.priority + "\n"
                + this.duration + "\n"
                + this.system;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(this.duration.get(), task.duration.get())
                && Objects.equals(name, task.name)
                && type == task.type
                && state == task.state
                && priority == task.priority
                && system == task.system;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, state, priority, duration.get(), system);
    }
}
