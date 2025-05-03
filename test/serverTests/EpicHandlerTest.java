package test.serverTests;

import com.google.gson.Gson;
import managerpackage.Managers;
import managerpackage.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {
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

    private HttpResponse<String> sendPostRequest(String url, String json) throws IOException, InterruptedException {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException { // вот сейчас с ним сидим
        Epic epic = new Epic("Test", "Description");
        String json = gson.toJson(epic);

        HttpResponse<String> response = sendPostRequest(
                "http://localhost:8080/epics",
                json
        );

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1");
        manager.addNewEpic(epic); // Метод void

        // Получаем ID через геттер объекта
        int epicId = epic.getId();

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics?id=" + epicId))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicId, responseEpic.getId());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1");
        manager.addNewEpic(epic); // Метод void

        // Получаем ID через геттер объекта
        int epicId = epic.getId();

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics?id=" + epicId))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, response.statusCode());
        Optional<Epic> epicOptional = manager.getEpic(epicId);
        assertTrue(epicOptional.isEmpty());
    }
}