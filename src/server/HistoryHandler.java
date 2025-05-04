package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managerpackage.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (GET.equals(exchange.getRequestMethod())) {
                List<Task> history = manager.getHistory();
                sendText(exchange, gson.toJson(history), OK);
            }
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка при получении истории: " + e.getMessage());
        }
    }
}
