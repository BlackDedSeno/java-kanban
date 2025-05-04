package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managerpackage.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    /*private final Gson gson;*/

    public SubtasksHandler(TaskManager manager/*, Gson gson*/) {
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
            ArrayList<SubTask> subtasks = manager.getAllSubTasks();
            sendText(exchange, GSON.toJson(subtasks), OK);
        } else {
            Optional<Integer> subTaskId = extractId(query);
            if (subTaskId.isPresent()) {
                Optional<SubTask> subTaskOpt = manager.getSubTask(subTaskId.get());
                if (subTaskOpt.isPresent()) {
                    String subTaskJson = GSON.toJson(subTaskOpt.get());
                    sendText(exchange, subTaskJson, OK);
                } else {
                    sendText(exchange, "Subtask not found", NOT_FOUND);
                }
            } else {
                sendText(exchange, "Invalid ID", BAD_REQUEST);
            }
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        SubTask subTask = GSON.fromJson(reader, SubTask.class);
        manager.addNewSubTask(subTask);
        sendText(exchange, "Subtask created", 201);
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        SubTask subTask = GSON.fromJson(reader, SubTask.class);
        manager.updateSubTask(subTask);
        sendText(exchange, "Subtask updated", 200);
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            manager.clearAllSubTasks();
            exchange.sendResponseHeaders(204, -1);
        } else {
            Optional<Integer> subTaskId = extractId(query);
            if (subTaskId.isPresent()) {
                manager.removeSubTaskById(subTaskId.get());
                exchange.sendResponseHeaders(204, -1);
            } else {
                sendText(exchange, "Invalid ID", BAD_REQUEST);
            }
        }
        exchange.close();
    }
}