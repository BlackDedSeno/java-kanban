package managerPackage;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

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
        updateEpic(epic);
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
        updateEpic(epic);

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
        subTasks.clear();

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
        if (subTasks.isEmpty()){
            System.out.println("Список задач пуст.");
        } else {
            int id = subTask.getId();
            subTasks.put(id, subTask);
            /*System.out.println(subTasks);*/
            Epic epic = epics.get(subTask.getepicID());
            updateEpic(epic);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.isEmpty()){
            System.out.println("Список задач пуст.");
        } else {
            int id = epic.getId();
            epics.put(id, epic);
        }

    }


    public void updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    private void calculateAndSetEpicStatus(Epic epic) {
        int newStatus = 0;
        int doneStatus = 0;
        ArrayList<Integer> ids = epic.getSubtasksIDs();
        if (ids.size() == 0)
            epic.setStatus(TaskStatus.NEW);

        for (int id : ids) {
            SubTask subTask = this.subTasks.get(id);
            switch (subTask.getStatus()) {
                case TaskStatus.NEW:
                    newStatus++;
                    break;
                case TaskStatus.DONE:
                    doneStatus++;
                    break;
                default:
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
            }
        }

        if (doneStatus == ids.size()) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        if (newStatus == ids.size()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

    }
}