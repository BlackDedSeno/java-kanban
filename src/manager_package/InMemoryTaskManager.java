package manager_package;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;
    private Map<Integer, Epic> epics;
    private int newId = 0;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void addNewTask(Task task) {
        int taskID = this.newId++;
        task.setID(taskID);
        tasks.put(taskID, task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        int epicID = this.newId++;
        epic.setID(epicID);
        epics.put(epicID, epic);
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        int taskID = this.newId++;
        subTask.setID(taskID);
        int epicID = subTask.getepicID();
        Epic epic = epics.get(epicID);
        epic.addSubtaskID(taskID);
        this.subTasks.put(taskID, subTask);
        updateEpic(epic);
    }

    @Override
    public String getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id).toString();

    }
    @Override
    public String getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id).toString();
    }

    @Override
    public String getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id).toString();
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        Epic epic = epics.get(id);
        ArrayList<SubTask> result = new ArrayList<>();
        for (int subtaskID : epic.getSubtasksIDs()) {
            result.add(subTasks.get(subtaskID));
        }
        return result;
    }

    @Override
    public void removeSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        int epicID = task.getepicID();
        Epic epic = epics.get(epicID);
        epic.removeSubIdByValue(id);
        subTasks.remove(id);
        updateEpic(epic);
    }

    @Override
    public void removeEpicById(int epicID) {
        Epic epic = epics.get(epicID);
        for (int subId : epic.getSubtasksIDs()) {
            subTasks.remove(subId);
        }
        epics.remove(epicID);

    }
    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
        subTasks.clear();

    }
    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubIDs();
            calculateAndSetEpicStatus(epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадача с ID " + id + " не найдена.");
        } else {
            subTasks.put(id, subTask);
            /*System.out.println(subTasks);*/
            Epic epic = epics.get(subTask.getepicID());
            if (epic != null) {
                updateEpic(epic);
            } else {
                System.out.println("Эпик с ID " + subTask.getepicID() + " не найден для подзадачи " + id);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с ID " + id + " не найден.");
        } else {
            epics.put(id, epic);
        }

    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    private void calculateAndSetEpicStatus(Epic epic) { //при изменении модификатора доступа возникает ошибка
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

    @Override
    public List getHistory() {
        return historyManager.getHistory();

    }
}