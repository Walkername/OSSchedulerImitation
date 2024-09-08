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

    private Priority priority;

    private AtomicInteger duration;

    public Task(String name, TaskType type, State state, Priority priority, int duration) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.priority = priority;
        this.duration = new AtomicInteger(duration);
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

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", priority=" + priority +
                ", duration=" + duration +
                '}';
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
                && priority == task.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, state, priority, duration.get());
    }
}
