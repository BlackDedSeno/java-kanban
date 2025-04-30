package managerpackage;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks;
    protected Map<Integer, SubTask> subTasks;
    protected Map<Integer, Epic> epics;
    protected int newId = 0;
    protected HistoryManager<Task> historyManager;

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId));

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        checkTimeOverlaps(task);
        int taskID = this.newId++;
        task.setID(taskID);
        tasks.put(taskID, task);
        addToPrioritized(task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        int epicID = this.newId++;
        epic.setID(epicID);
        epics.put(epicID, epic);
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        checkTimeOverlaps(subTask);
        int taskID = this.newId++;
        subTask.setID(taskID);
        int epicID = subTask.getepicID();
        Epic epic = epics.get(epicID);
        if (epic != null) {
            epic.addSubtaskID(taskID);
            subTasks.put(taskID, subTask);
            addToPrioritized(subTask);
            updateEpicTimes(epic);
            updateEpic(epic);
        }
    }

    private void updateEpicTimes(Epic epic) {
        List<SubTask> epicSubTasks = getEpicSubTasks(epic.getId());
        epic.calculateTimes(epicSubTasks);
    }

    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }


    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    private void checkTimeOverlaps(Task newTask) {
        if (newTask.getStartTime() == null) {
            return;
        }

        boolean hasOverlap = getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null)
                .anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));

        if (hasOverlap) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей задачей");
        }
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
        return epics.get(id).getSubtasksIDs().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void removeSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        removeFromPrioritized(task);
        int epicID = task.getepicID();
        Epic epic = epics.get(epicID);
        epic.removeSubIdByValue(id);
        subTasks.remove(id);
        historyManager.remove(id);
        updateEpic(epic);
    }

    @Override
    public void removeEpicById(int epicID) {
        Epic epic = epics.get(epicID);
        epic.getSubtasksIDs().forEach(subId -> {
            removeFromPrioritized(subTasks.get(subId));
            subTasks.remove(subId);
            historyManager.remove(subId);
        });
        epics.remove(epicID);
        historyManager.remove(epicID);
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.remove(id);
        Task removedTask = tasks.remove(id);
        tasks.remove(id);
        if (removedTask != null) {
            removeFromPrioritized(removedTask);
        }
    }

    @Override
    public void clearAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.values().forEach(subTask -> {
            removeFromPrioritized(subTask);
            historyManager.remove(subTask.getId());
        });
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void clearAllSubTasks() {
        subTasks.values().forEach(subTask -> {
            removeFromPrioritized(subTask);
            historyManager.remove(subTask.getId());
        });
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubIDs();
            calculateAndSetEpicStatus(epic);
        });
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадача с ID " + id + " не найдена.");
        } else {
            checkTimeOverlaps(subTask);
            removeFromPrioritized(subTasks.get(subTask.getId()));
            subTasks.put(id, subTask);
            addToPrioritized(subTask);
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