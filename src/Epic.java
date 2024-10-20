
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIDs;

    public Epic(String name, String descriprion) {
        super(name, descriprion);
        subtaskIDs = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        this.id = id;
        subtaskIDs = new ArrayList<>();
    }

    public void setStatus(TaskStatus status){
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

    @Override
    public String toString() {
        return "Epic{" +
                " id=" + this.id + " " +
                ", name=" + this.name + " " +
                ", description=" + this.description + " " +
                ", subtaskIDs=" + this.subtaskIDs.toString() + " " +
                ", status=" + this.status + " " +
                "}";
    }

}