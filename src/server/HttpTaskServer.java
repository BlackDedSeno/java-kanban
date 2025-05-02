package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import managerpackage.Managers;
import managerpackage.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager manager;
    private final int port;
    private static final Gson gson = new Gson();

    // Основной конструктор, позволяет задавать порт
    public HttpTaskServer(TaskManager manager, int port) throws IOException {
        this.manager = manager;
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        setupContexts();
    }



    // Старый конструктор по умолчанию на 8080
    public HttpTaskServer(TaskManager manager) throws IOException {
        this(manager, 8080); // по умолчанию порт 8080
    }

    public void start() {
        System.out.println("запускается сервер");
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + getPort());
    }

    public void stop() {
        System.out.println("Останавливаем сервер");
        server.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

    public int getPort() {
        return server.getAddress().getPort(); // ← фактически используемый порт
    }

    public static Gson getGson() {
        return gson;
    }

    private void setupContexts() {
        server.createContext("/tasks", new TasksHandler(manager, gson));
        server.createContext("/subtasks", new SubtasksHandler(manager, gson));
        server.createContext("/epics", new EpicsHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer(Managers.getDefault()).start();
    }
}