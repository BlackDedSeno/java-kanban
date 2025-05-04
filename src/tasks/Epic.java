package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIDs;
    private LocalDateTime endTime;


    public Epic(String name, String descriprion) {
        super(name, descriprion);
        this.startTime = null; // Явная инициализация
        this.duration = Duration.ZERO;
        subtaskIDs = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        this.id = id;
        subtaskIDs = new ArrayList<>();
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void addSubtaskID(int newSubtaskID) {
        this.subtaskIDs.add(newSubtaskID);
    }

    public ArrayList<Integer> getSubtasksIDs() {
        return this.subtaskIDs;
    }

    public void clearSubIDs() {
        subtaskIDs.clear();
    }

    public void removeSubIdByValue(int v) {
        this.subtaskIDs.remove(Integer.valueOf(v));
    }

    public void calculateTimes(List<SubTask> subTasks) {
        if (subTasks == null || subTasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            this.endTime = null;
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (SubTask subTask : subTasks) {
            if (subTask != null && subTask.getStartTime() != null) {
                if (earliestStart == null || subTask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subTask.getStartTime();
                }

                LocalDateTime subTaskEnd = subTask.getEndTime();
                if (latestEnd == null || subTaskEnd.isAfter(latestEnd)) {
                    latestEnd = subTaskEnd;
                }

                totalDuration = totalDuration.plus(subTask.getDuration());
            }
        }

        this.startTime = earliestStart;
        this.duration = totalDuration;
        this.endTime = latestEnd;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime : "null") +
                ", duration=" + duration.toMinutes() + " minutes" +
                ", endTime=" + (endTime != null ? endTime : "null") +
                ", subtaskIDs=" + subtaskIDs +
                '}';
    }

}