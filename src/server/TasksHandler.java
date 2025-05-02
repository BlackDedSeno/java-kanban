package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managerpackage.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        switch (method) {
            case "GET":
                handleGet(exchange, query);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange, query);
                break;
            default:
                sendText(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            ArrayList<Task> tasks = manager.getAllTasks();
            sendText(exchange, gson.toJson(tasks), 200);
        } else {
            Optional<Integer> taskId = extractId(query);
            if (taskId.isPresent()) {
                Optional<Task> taskOpt = manager.getTask(taskId.get());
                if (taskOpt.isPresent()) {
                    String taskJson = gson.toJson(taskOpt.get());
                    sendText(exchange, taskJson, 200);
                } else {
                    sendText(exchange, "Task not found", 404);
                }
            } else {
                sendText(exchange, "Invalid ID", 400);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(reader, Task.class);

        if (task == null || task.getName() == null || task.getName().isEmpty()) { // Используем getName() вместо getTitle()
            sendText(exchange, "Invalid task data", 400);  // Статус 400 для неправильных данных
            return;
        }

        manager.addNewTask(task);
        sendText(exchange, "Task created", 201);
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(reader, Task.class);
        manager.updateTask(task);
        sendText(exchange, "Task updated", 200);
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            manager.clearAllTasks();
            sendText(exchange, "All tasks deleted", 200);
        } else {
            Optional<Integer> taskId = extractId(query);
            if (taskId.isPresent()) {
                manager.removeTaskById(taskId.get());
                sendText(exchange, "Task deleted", 200);
            } else {
                sendText(exchange, "Invalid ID", 400);
            }
        }
    }
}
