package tasks;

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

    public int getepicID() {
        return this.epicID;
    }

    @Override
    public String toString() {
        return "Tasks.SubTask{" +
                " id=" + this.id + " " +
                ", name=" + this.name + " " +
                ", description=" + this.description + " " +
                ", epicID=" + this.epicID + " " +
                ", status=" + this.status + " " +
                "}";
    }
}