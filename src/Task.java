

import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(String name, String description) {
        this.description = description;
        this.name = name;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, int id, TaskStatus status) {
        this.description = description;
        this.name = name;
        this.status = status;
        this.id = id;
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

    @Override
    public String toString() {
        return "Task{" +
                " id='" + this.id + "'" +
                ", name='" + this.name + "'" +
                ", description='" + this.description + "'" +
                ", status='" + this.status + "'" +
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
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }
}