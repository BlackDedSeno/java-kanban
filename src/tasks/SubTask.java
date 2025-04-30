package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicID;

    public SubTask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, int id, int epicID, TaskStatus status) {
        super(name, description, id, status);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, int id, int epicID,
                   TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicID = epicID;
    }

    public int getepicID() {
        return this.epicID;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime : "null") +
                ", duration=" + duration.toMinutes() + " minutes" +
                ", epicID=" + epicID +
                '}';
    }
}