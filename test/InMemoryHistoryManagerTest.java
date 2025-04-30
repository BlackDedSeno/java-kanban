package test;

import managerpackage.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    @Test
    void addTaskAddedToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача 1", "Описание 1");
        task.setID(1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task), "Список истории должен содержать одну задачу.");
    }

    @Test
    void addDuplicateTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setID(1);
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Список истории должен содержать одну задачу.");
        assertEquals(task1, history.get(0), "В списке должна быть первая задача.");
    }

    @Test
    void removeTaskExists() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setID(1);
        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setID(2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Список истории должен содержать одну задачу.");
        assertFalse(history.contains(task1), "Задача должна быть удалена.");
        assertTrue(history.contains(task2), "В списке должна быть вторая задача.");
    }
}
