package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.description = description;
        this.name = name;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ZERO;
    }

    public Task(String name, String description, int id, TaskStatus status) {
        this.description = description;
        this.name = name;
        this.status = status;
        this.id = id;
        this.duration = Duration.ZERO;
    }

    public Task(String name, String description, int id, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        this(name, description, id, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + this.id + "'" +
                ", name='" + this.name + "'" +
                ", description='" + this.description + "'" +
                ", status='" + this.status + "'" +
                ", startTime='" + (startTime != null ? startTime.toString() : "null") + "'" +
                ", duration='" + duration.toMinutes() + "'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration, startTime);
    }
}