package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managerpackage.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Task> prioritized = List.copyOf(manager.getPrioritizedTasks());
                sendText(exchange, gson.toJson(prioritized), OK);
            } else {
                exchange.sendResponseHeaders(METHOD_NOT_ALLOWED, 0);
                exchange.close();
            }
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка при получении задач по приоритету: " + e.getMessage());
        }
    }
}
