package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.TaskManager;


import java.util.regex.Pattern;

public class HistoryHandler extends Handler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            String response;
            final String path = exchange.getRequestURI().getPath();
            if (exchange.getRequestMethod().equals("GET")) {
                if (Pattern.matches("^/history$", path)) {
                    response = gson.toJson(manager.getHistory());
                    sendResponse(exchange, response);
                } else {
                    exchange.sendResponseHeaders(500, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
