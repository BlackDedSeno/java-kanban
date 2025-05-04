package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String DELETE = "DELETE";

    protected static final int OK = 200;
    protected static final int CREATED = 201;
    protected static final int BAD_REQUEST = 400;
    protected static final int NOT_FOUND = 404;
    protected static final int METHOD_NOT_ALLOWED = 405;
    protected static final int INTERNAL_SERVER_ERROR = 500;

    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected void sendText(HttpExchange exchange, String responseText, int statusCode) throws IOException {
        byte[] resp = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendSuccess(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 201);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + message + "\"}", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + message + "\"}", 406);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, INTERNAL_SERVER_ERROR);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected Optional<Integer> extractId(String query) {
        if (query == null) return Optional.empty();

        String[] parts = query.split("=");
        if (parts.length == 2 && "id".equals(parts[0])) {
            try {
                return Optional.of(Integer.parseInt(parts[1]));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}