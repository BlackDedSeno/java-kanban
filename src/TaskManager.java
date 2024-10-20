
import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Epic> epics;
    private int newId = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void addNewTask(Task task) {
        int taskID = this.newId++;
        task.setID(taskID);
        tasks.put(taskID, task);
    }

    public void addNewEpic(Epic epic) {
        int epicID = this.newId++;
        epic.setID(epicID);
        epics.put(epicID, epic);
    }

    public void addNewSubTask(SubTask subTask) {
        int taskID = this.newId++;
        subTask.setID(taskID);
        int epicID = subTask.getepicID();
        Epic epic = epics.get(epicID);
        epic.addSubtaskID(taskID);
        this.subTasks.put(taskID, subTask);
        generateNewAndUpdateEpic(epic);
    }

    public String getTask(int id) {
        return tasks.get(id).toString();
    }

    public String getSubTask(int id) {
        return subTasks.get(id).toString();
    }

    public String getEpic(int id) {
        return epics.get(id).toString();
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        Epic epic = epics.get(id);
        ArrayList<SubTask> result = new ArrayList<>();
        for (int subtaskID : epic.getSubtasksIDs()) {
            result.add(subTasks.get(subtaskID));
        }
        return result;
    }

    public void removeSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        int epicID = task.getepicID();
        Epic epic = epics.get(epicID);
        epic.removeSubIdByValue(id);
        subTasks.remove(id);
        generateNewAndUpdateEpic(epic);

    }

    public void removeEpicById(int epicID) {
        Epic epic = epics.get(epicID);
        for (int subId : epic.getSubtasksIDs()) {
            subTasks.remove(subId);
        }
        epics.remove(epicID);


    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void clearAllEpics() {
        epics.clear();

    }

    public void clearAllTasks() {
        tasks.clear();
    }

    public void clearAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubIDs();
            calculateAndSetEpicStatus(epic);
        }
    }

    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        subTasks.put(id, subTask);
        System.out.println(subTasks);
        Epic epic = epics.get(subTask.getepicID());
        generateNewAndUpdateEpic(epic);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        epics.put(id, epic);
    }

    public void generateNewAndUpdateEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
        ArrayList<Integer> subIDs = epic.getSubtasksIDs();
        for (Integer subId : subIDs) {
            newEpic.addSubtaskID(subId);
        }
        calculateAndSetEpicStatus(newEpic);
        updateEpic(newEpic);
    }

    public void updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    public void calculateAndSetEpicStatus(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;
        ArrayList<Integer> IDs = epic.getSubtasksIDs();
        if (IDs.size() == 0)
            epic.setStatus(TaskStatus.NEW);

        for (int id : IDs) {
            SubTask subTask = this.subTasks.get(id);
            switch (subTask.getStatus()) {
                case NEW:
                    newStatus++;
                    break;
                case DONE:
                    doneStatus++;
                    break;
                default:
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
            }
        }

        if (doneStatus == IDs.size()) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        if (newStatus == IDs.size()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

    }
}