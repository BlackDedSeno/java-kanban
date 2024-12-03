package test;

import managerPackage.InMemoryTaskManager;
import managerPackage.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    @Test
    void testAddNewTask() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        assertEquals(1, manager.getAllTasks().size());
        assertNotNull(manager.getTask(task1.getId()));
    }

    @Test
    void testAddNewTaskNullValues() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task(null, null);
        manager.addNewTask(task1);
        assertEquals(1, manager.getAllTasks().size());
        assertNotNull(manager.getTask(task1.getId()));
    }


    @Test
    void testAddNewEpic() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        assertEquals(1, manager.getAllEpics().size());
        assertNotNull(manager.getEpic(epic1.getId()));
    }

    @Test
    void testAddNewEpicNullValues() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic(null, null);
        manager.addNewEpic(epic1);
        assertEquals(1, manager.getAllEpics().size());
        assertNotNull(manager.getEpic(epic1.getId()));
    }

    @Test
    void testAddNewSubTask() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        assertEquals(1, manager.getAllSubTasks().size());
        assertNotNull(manager.getSubTask(subTask1.getId()));
    }

    @Test
    void testAddNewSubTaskNullValues() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask(null, null, epic1.getId());
        manager.addNewSubTask(subTask1);
        assertEquals(1, manager.getAllSubTasks().size());
        assertNotNull(manager.getSubTask(subTask1.getId()));

    }

    @Test
    void testRemoveSubTaskById() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        manager.removeSubTaskById(subTask1.getId());
        assertEquals(0, manager.getAllSubTasks().size());

    }
    @Test
    void testRemoveEpicById() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        manager.removeEpicById(epic1.getId());
        assertEquals(0, manager.getAllEpics().size());

    }

    @Test
    void testGetAllTasks() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0));
    }

    @Test
    void testGetAllEpics() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals(epic1, epics.get(0));
    }

    @Test
    void testGetAllSubTasks() {
        TaskManager manager = new InMemoryTaskManager();
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
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        manager.clearAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void testClearAllEpics() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        manager.clearAllEpics();
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void testClearAllSubTasks() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addNewSubTask(subTask1);
        manager.clearAllSubTasks();
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    void testGetHistory(){
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        manager.getTask(task1.getId());
        assertFalse(manager.getHistory().isEmpty());
    }



    }
