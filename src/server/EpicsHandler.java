package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managerpackage.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler  {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
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
            ArrayList<Epic> epics = manager.getAllEpics();
            sendText(exchange, gson.toJson(epics), 200);
        } else {
            Optional<Integer> epicId = extractId(query);
            if (epicId.isPresent()) {
                Optional<Epic> epicOpt = manager.getEpic(epicId.get());
                if (epicOpt.isPresent()) {
                    String epicJson = gson.toJson(epicOpt.get());
                    sendText(exchange, epicJson, 200);
                } else {
                    sendText(exchange, "Epic not found", 404);
                }
            } else {
                sendText(exchange, "Invalid ID", 400);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(reader, Epic.class);

        if (epic != null) {
            manager.addNewEpic(epic);
            sendText(exchange, "Epic created", 201);
        } else {
            sendBadRequest(exchange, "Invalid Epic JSON");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(reader, Epic.class);
        manager.updateEpic(epic);
        sendText(exchange, "Epic updated", 200);
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        Optional<Integer> epicId = extractId(query);
        if (epicId.isEmpty()) {
            sendText(exchange, "Invalid ID", 400);
            return;
        }

        int id = epicId.get();
        Optional<Epic> epic = manager.getEpic(id);

        if (epic.isEmpty()) {
            sendText(exchange, "Epic not found", 404);
            return;
        }

        manager.removeEpicById(id);
        exchange.sendResponseHeaders(204, -1);
    }

}

