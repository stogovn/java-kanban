package http.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.util.regex.Pattern;

public class PrioritizedHandler extends Handler {

    public PrioritizedHandler(TaskManager manager) {
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
            if (Pattern.matches("^/prioritized$", path)) {
                response = gson.toJson(manager.getPrioritizedTasks());
                sendResponse(exchange, response);
            } else {
                exchange.sendResponseHeaders(500, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
