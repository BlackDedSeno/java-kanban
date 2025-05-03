package test.serverTests;

import com.google.gson.Gson;
import managerpackage.Managers;
import managerpackage.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;
    private int port;


    @BeforeEach
    void setUp() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager, 0); // Автовыбор порта
        port = server.getPort(); // Получаем реальный порт
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        server.start();
        waitForServerStart(port);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addNewEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("Sub", "Desc", epicId);
        String json = gson.toJson(subTask);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/subtasks")) // Исправленный URL
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size());
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        // Создаем эпик и подзадачу
        Epic epic = new Epic("Epic", "Description");
        manager.addNewEpic(epic);
        int epicId = epic.getId();

        SubTask subtask = new SubTask("Sub", "Desc", epicId);
        manager.addNewSubTask(subtask);
        int subId = subtask.getId();

        // Отправляем DELETE-запрос
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/subtasks?id=" + subId))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, response.statusCode());
        assertTrue(manager.getSubTask(subId).isEmpty());
    }

    private void waitForServerStart(int port) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000) { // Таймаут 5 секунд
            if (isPortInUse(port)) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        throw new RuntimeException("Сервер не запустился за 5 секунд");
    }

    private boolean isPortInUse(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
