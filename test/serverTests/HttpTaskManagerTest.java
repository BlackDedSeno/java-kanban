
package test.serverTests;

import com.google.gson.Gson;
import managerpackage.InMemoryTaskManager;
import managerpackage.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected Gson gson;
    protected HttpClient client;

    // Убедимся, что сервер запускается правильно
    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager = new InMemoryTaskManager();
        int port = 8080; // Используем фиксированный порт
        server = new HttpTaskServer(manager, port); // Передаём порт в конструктор
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        server.start();
        waitForServerToStart(port); // Проверяем доступность сервера
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    // Добавим таймаут и проверку доступности сервера
    private void waitForServerToStart(int port) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000) { // таймаут 5 секунд
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/").openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode < 500) {
                    return;
                }
            } catch (IOException ignored) {
                Thread.sleep(200);
            }
        }
        throw new RuntimeException("Server did not start within the timeout period.");
    }


    @Test
    void testHandleGetAllTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("[]"), "Expected empty task list");
    }



    // Тестируем POST-запрос для создания задачи
    @Test
    void testHandlePostTask() throws Exception {
        String taskJson = "{\"id\":1, \"title\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Task should be created successfully");
        assertTrue(response.body().contains("Task created"), "Response should indicate task creation");
    }

    // Тестируем GET-запрос для получения задачи по ID
    @Test
    void testHandleGetTaskById() throws Exception {
        // Сначала создаём задачу
        String taskJson = "{\"id\":1, \"title\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString()); // Отправляем запрос

        // Теперь пытаемся получить эту задачу по ID
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\":1"), "Response should contain task with ID 1");
    }

    // Тестируем DELETE-запрос для удаления задачи
    @Test
    void testHandleDeleteTask() throws Exception {
        // Сначала создаём задачу
        String taskJson = "{\"id\":1, \"title\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        Thread.sleep(1000);
        client.send(postRequest, HttpResponse.BodyHandlers.ofString()); // Отправляем запрос


        // Теперь удаляем задачу
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task deleted"), "Response should indicate task deletion");
    }

    // Тестируем некорректный GET-запрос для задачи с несуществующим ID
    @Test
    void testHandleInvalidGetTaskById() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Task with ID 999 should not exist");
        assertTrue(response.body().contains("Task not found"), "Response should indicate task not found");
    }

    // Тестируем POST-запрос с неверным JSON
    @Test
    void testHandlePostTaskWithInvalidJson() throws Exception {
        String invalidJson = "{\"id\":, \"title\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Invalid JSON should return 400 status");
        assertTrue(response.body().contains("Invalid JSON"), "Response should indicate invalid JSON");
    }

    // Тестируем DELETE-запрос для удаления всех задач
    @Test
    void testHandleDeleteAllTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("All tasks deleted"), "Response should indicate all tasks were deleted");
    }

    @Test
    void testCreateTask() throws Exception {
        String taskJson = "{\"id\":1, \"name\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Task should be created");
        assertTrue(response.body().contains("Task created"), "Response should indicate task creation");
    }

    @Test
    void testGetTask() throws Exception {
        String taskJson = "{\"id\":1, \"name\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        // Создаём задачу
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Получаем задачу
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Should return task details");
        assertTrue(response.body().contains("Test Task"), "Response should contain task name");
    }

    @Test
    void testUpdateTask() throws Exception {
        String taskJson = "{\"id\":1, \"name\":\"Test Task\", \"description\":\"Updated Description\", \"status\":\"IN_PROGRESS\"}";

        // Создаём задачу
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":1, \"name\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}"))
                .header("Content-Type", "application/json")
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Обновляем задачу
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .PUT(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Task should be updated");
        assertTrue(response.body().contains("Task updated"), "Response should indicate task update");
    }

    @Test
    void testDeleteTask() throws Exception {
        String taskJson = "{\"id\":1, \"name\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";

        // Создаём задачу
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Удаляем задачу
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Task should be deleted");
        assertTrue(response.body().contains("Task deleted"), "Response should indicate task deletion");
    }

    @Test
    void testCreateEpic() throws Exception {
        String epicJson = "{\"id\":1, \"name\":\"Test Epic\", \"description\":\"Epic description\"}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Epic should be created");
        assertTrue(response.body().contains("Epic created"), "Response should indicate epic creation");
    }

    @Test
    void testGetHistory() throws Exception {
        // Создаём задачу
        String taskJson = "{\"id\":1, \"name\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Получаем историю
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Should return history details");
        assertTrue(response.body().contains("Test Task"), "Response should contain task history");
    }

    @Test
    void testGetPrioritizedTasks() throws Exception {
        // Создаём задачу
        String taskJson = "{\"id\":1, \"name\":\"Test Task\", \"description\":\"Description\", \"status\":\"NEW\"}";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Получаем приоритетные задачи
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Should return prioritized tasks");
        assertTrue(response.body().contains("Test Task"), "Response should contain task name");
    }


}

