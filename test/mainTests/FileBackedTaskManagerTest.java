package test.mainTests;

import managerpackage.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @TempDir
    File tempDir;

    private File file;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        file = new File(tempDir, "tasks.csv");
        return new FileBackedTaskManager(file);
    }

    @Test
    void testEmptyFileLoad() throws IOException {
        File emptyFile = new File(tempDir, "empty.csv");
        emptyFile.createNewFile();
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(emptyFile);
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testSaveAndLoadTasks() {
        FileBackedTaskManager manager = createTaskManager();

        Task task = new Task("Тестовая задача", "Описание");
        manager.addNewTask(task);

        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask("Подзадача", "Описание", epic.getId());
        manager.addNewSubTask(subTask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubTasks().size());
    }

    @Test
    void testBrokenFileLoad() throws IOException {
        File brokenFile = new File(tempDir, "broken.csv");
        Files.writeString(brokenFile.toPath(), "id,type,name,status,description,epic\nbroken,data,line");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager.loadFromFile(brokenFile);
        });

        assertTrue(exception.getMessage().contains("Некорректный формат строки"));
    }

}