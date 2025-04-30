package test;

import managerpackage.FileBackedTaskManager;
import managerpackage.InMemoryHistoryManager;
import managerpackage.InMemoryTaskManager;
import managerpackage.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
}