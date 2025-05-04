package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
    /*private final Gson gson;*/

    public TasksHandler(TaskManager manager/*, Gson gson*/) {
        this.manager = manager;
        /*this.gson = gson;*/
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
                sendText(exchange, "Method Not Allowed", METHOD_NOT_ALLOWED);
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            ArrayList<Task> tasks = manager.getAllTasks();
            sendText(exchange, GSON.toJson(tasks), OK);
        } else {
            Optional<Integer> taskId = extractId(query);
            if (taskId.isPresent()) {
                Optional<Task> taskOpt = manager.getTask(taskId.get());
                if (taskOpt.isPresent()) {
                    String taskJson = GSON.toJson(taskOpt.get());
                    sendText(exchange, taskJson, OK);
                } else {
                    sendText(exchange, "Task not found", NOT_FOUND);
                }
            } else {
                sendText(exchange, "Invalid ID", BAD_REQUEST);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Received JSON: " + json);

            Task task = GSON.fromJson(json, Task.class);

            // Проверка обязательных полей
            if (task.getName() == null || task.getName().trim().isEmpty()) {
                sendBadRequest(exchange, "Task name is required");
                return;
            }

            manager.addNewTask(task);
            sendText(exchange, "Task created", 201);

        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Invalid JSON format");
        } catch (Exception e) {
            System.err.println("Error processing task: " + e.getMessage());
            sendServerError(exchange, "Internal server error");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Task task = GSON.fromJson(reader, Task.class);
        manager.updateTask(task);
        sendText(exchange, "Task updated", 200);
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            manager.clearAllSubTasks();
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        } else {
            Optional<Integer> subTaskId = extractId(query);
            if (subTaskId.isPresent()) {
                manager.removeSubTaskById(subTaskId.get());
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
            } else {
                sendText(exchange, "Invalid ID", 400);
            }
        }
    }
}
