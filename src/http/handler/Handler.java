package http.handler;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class Handler implements HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;

    public Handler(TaskManager manager) {
        this.manager = manager;
        gson = Managers.getGson();
    }

    protected String readResponse(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), UTF_8);
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
        }
        return null;
    }

    protected void sendResponse(HttpExchange exchange, String responseBody) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            byte[] response = responseBody.getBytes(UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            os.write(response);
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
        }
    }

    protected int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
