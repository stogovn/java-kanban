package http.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.util.regex.Pattern;

public class HistoryHandler extends Handler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            String response;
            final String path = exchange.getRequestURI().getPath();
            if (!exchange.getRequestMethod().equals("GET")) {
                exchange.sendResponseHeaders(405, 0);
                return;
            }
            if (Pattern.matches("^/history$", path)) {
                response = gson.toJson(manager.getHistory());
                sendResponse(exchange, response);
            } else {
                exchange.sendResponseHeaders(500, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
