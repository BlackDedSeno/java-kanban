package test.serverTests;

import com.google.gson.Gson;
import managerpackage.InMemoryTaskManager;
import managerpackage.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;
    private int port;


    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager, 0); // Автовыбор порта
        port = server.getPort();
        server.start();
        waitForServerToStart(port);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
            /*waitForServerToStop(port);*/
        }
    }

    @Test
    void shouldStartServerOnSpecifiedPort() throws IOException {
        try (HttpTaskServer testServer = new HttpTaskServer(manager, 0)) {
            testServer.start();
            assertTrue(isPortInUse(testServer.getPort()));
        }
    }

    @Test
    void shouldStartOnRandomPort() {
        assertTrue(port > 0, "Порт должен быть больше 0");
    }

    @Test
    void shouldRegisterAllHandlers() throws IOException {
        server = new HttpTaskServer(manager, 0); // Автовыбор порта
        int port = server.getPort();
        server.start();

        HttpClient client = HttpClient.newHttpClient();
        assertDoesNotThrow(() -> {
            client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:" + port + "/tasks"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        }, "Обработчик /tasks не зарегистрирован");

        server.stop();
    }

    private void waitForServerToStart(int port) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 30_000) {
            if (isPortInUse(port)) {
                return;
            }
            Thread.sleep(500);
        }
        throw new RuntimeException("Сервер не запустился за 30 секунд");
    }

    private void waitForServerToStop(int port) {
        int timeout = 30_000;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeout) {
            if (!isPortInUse(port)) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        throw new RuntimeException("Сервер не остановился за 30 секунд");
    }
    private boolean isPortInUse(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

}
