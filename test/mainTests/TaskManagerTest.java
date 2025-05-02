package test.mainTests;

import managerpackage.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setup() {
        manager = createTaskManager();
    }

    @Test
    void testAddNewTask() {
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void testAddNewTaskNullValues() {
        Task task1 = new Task(null, null);
        manager.addNewTask(task1);
        assertNotNull(manager.getTask(task1.getId()));
    }


    @Test
    void testAddNewEpic() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void testAddNewEpicNullValues() {
        Epic epic1 = new Epic(null, null);
        manager.addNewEpic(epic1);
        assertNotNull(manager.getEpic(epic1.getId()));
    }

    @Test
    void testAddNewSubTask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        assertEquals(1, manager.getAllSubTasks().size());
    }

    @Test
    void testAddNewSubTaskNullValues() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask(null, null, epic1.getId());
        manager.addNewSubTask(subTask1);
        assertNotNull(manager.getSubTask(subTask1.getId()));

    }

    @Test
    void testRemoveSubTaskById() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        manager.removeSubTaskById(subTask1.getId());
        assertEquals(0, manager.getAllSubTasks().size());

    }

    @Test
    void testRemoveEpicById() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        manager.removeEpicById(epic1.getId());
        assertEquals(0, manager.getAllEpics().size());

    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0));
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals(epic1, epics.get(0));
    }

    @Test
    void testGetAllSubTasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        List<SubTask> subTasks = manager.getAllSubTasks();
        assertEquals(1, subTasks.size());
        assertEquals(subTask1, subTasks.get(0));
    }

    @Test
    void testClearAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        manager.clearAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void testClearAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        manager.clearAllEpics();
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void testClearAllSubTasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        manager.clearAllSubTasks();
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        manager.getTask(task1.getId());
        assertFalse(manager.getHistory().isEmpty());
    }

    @Test
    void testEpicTimeCalculation() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic", "Description");
        manager.addNewEpic(epic);

        LocalDateTime now = LocalDateTime.now();

        SubTask sub1 = new SubTask("Sub1", "Desc1", epic.getId());
        sub1.setStartTime(now);
        sub1.setDuration(Duration.ofHours(1));
        manager.addNewSubTask(sub1);

        SubTask sub2 = new SubTask("Sub2", "Desc2", epic.getId());
        sub2.setStartTime(now.plusHours(2));
        sub2.setDuration(Duration.ofHours(3));
        manager.addNewSubTask(sub2);

        assertEquals(now, epic.getStartTime());
        assertEquals(now.plusHours(5), epic.getEndTime());
        assertEquals(Duration.ofHours(4), epic.getDuration());
    }

    @Test
    void testPrioritizedTasks() {
        TaskManager manager = Managers.getDefault();
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1");
        task1.setStartTime(now.plusHours(3));
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task2", "Desc2");
        task2.setStartTime(now.plusHours(1));
        task2.setDuration(Duration.ofHours(1));

        Task task3 = new Task("Task3", "Desc3");

        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }
}