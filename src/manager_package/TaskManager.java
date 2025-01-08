package manager_package;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubTasks();

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubTask(SubTask subTask);

    String getTask(int id);

    String getSubTask(int id);

    String getEpic(int id);

    ArrayList<SubTask> getEpicSubTasks(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int epicID);

    void removeTaskById(int id);

    void clearAllEpics();

    void clearAllTasks();

    void clearAllSubTasks();

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void updateTask(Task task);

    List getHistory();

}
