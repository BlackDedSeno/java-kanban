package test;

import managerpackage.FileBackedTaskManager;
import managerpackage.InMemoryHistoryManager;
import managerpackage.InMemoryTaskManager;
import managerpackage.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    @Test
    void testAddNewTask() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void testAddNewTaskNullValues() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task(null, null);
        manager.addNewTask(task1);
        assertNotNull(manager.getTask(task1.getId()));
    }


    @Test
    void testAddNewEpic() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void testAddNewEpicNullValues() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic(null, null);
        manager.addNewEpic(epic1);
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
    }

    @Test
    void testAddNewSubTaskNullValues() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask(null, null, epic1.getId());
        manager.addNewSubTask(subTask1);
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
    void testGetHistory() {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        manager.addNewTask(task1);
        manager.getTask(task1.getId());
        assertFalse(manager.getHistory().isEmpty());
    }

    @Test
    void addTaskAddedToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача 1", "Описание 1");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task), "Список истории должен создержать одну задачу.");
    }

    @Test
    void addDuplicateTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Список истории должен создержать одну задачу.");
        assertEquals(task1, history.get(0), "В списке должна быть первая задача");
    }

    @Test
    void removeTaskExists() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Список истории должен содержать одну задачу");
        assertFalse(history.contains(task1), "Задача должна быть удалена");
        assertTrue(history.contains(task2), "В списке должна быть вторая задача");
    }

    @TempDir
    Path tempDir;

    @Test
    void testEmptyFileLoad() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        Files.createFile(file);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file.toFile());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testSaveAndLoadTasks(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("tasks.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Тестовая задача", "Описание");
        manager.addNewTask(task);

        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask("Тестовая подзадача", "Описание подзадачи", epic.getId());
        manager.addNewSubTask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubTasks().size());
    }

    @Test
    void testBrokenFileLoad(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("broken.csv").toFile();
        Files.writeString(file.toPath(), "id,type,name,status,description,epic\nbroken,data,line");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        });

        String expectedMessage = "Некорректный формат строки";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
    }
