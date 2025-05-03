package test.serverTests;

import com.google.gson.Gson;
import managerpackage.Managers;
import managerpackage.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Desc");
        String json = gson.toJson(task);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        // Создаем и добавляем задачу
        Task task = new Task("Old", "Desc");
        manager.addNewTask(task);
        int taskId = task.getId(); // Получаем ID после добавления

        // Создаем обновленную версию задачи (с тем же ID)
        Task updated = new Task(
                "New",
                "Обновлённое описание",
                taskId,
                TaskStatus.IN_PROGRESS, // Пример статуса
                LocalDateTime.now().plusHours(1), // Новое время
                Duration.ofMinutes(45) // Новая длительность
        );

        String json = gson.toJson(updated);

        // Отправляем PUT-запрос (или POST, если у вас используется POST для обновлений)
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .PUT(HttpRequest.BodyPublishers.ofString(json)) // Используйте PUT если реализовано
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Неверный код ответа при обновлении");

        // Проверяем обновленные данные
        Optional<Task> updatedTask = manager.getTask(taskId);
        assertTrue(updatedTask.isPresent(), "Задача должна существовать");
        assertEquals("New", updatedTask.get().getName(), "Имя не обновилось");
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.get().getStatus(), "Статус не изменился");
    }
}
