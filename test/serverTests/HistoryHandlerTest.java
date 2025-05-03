package test.serverTests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managerpackage.Managers;
import managerpackage.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
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
    void testGetHistory() throws IOException, InterruptedException {
        /*int taskId = manager.addNewTask(new Task("Task", "Desc"));*/

        Task task = new Task("Эпик 1", "Описание 1");
        manager.addNewTask(task); // Метод void

        // Получаем ID через геттер объекта
        int taskId = task.getId();
        manager.getTask(taskId); // Добавляем в историю

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/history"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, history.size());
    }
}