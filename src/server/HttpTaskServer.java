package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managerpackage.Managers;
import managerpackage.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import static server.BaseHttpHandler.GSON;

public class HttpTaskServer implements AutoCloseable {
    private final HttpServer server;
    private final TaskManager manager;
    private final int port;
    /*private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();*/

    // Основной конструктор, позволяет задавать порт
    public HttpTaskServer(TaskManager manager, int port) throws IOException {
        this.manager = manager;
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        setupContexts();
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this(manager, 8080);
    }

    public void start() {
        System.out.println("запускается сервер");
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + getPort());
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Сервер остановлен на порту " + getPort());
        }
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public static Gson getGson() {
        return GSON;
    }

    private void setupContexts() {
        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer(Managers.getDefault()).start();
    }

    @Override
    public void close() {
        stop();
    }
}