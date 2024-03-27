package http;

import com.sun.net.httpserver.HttpServer;
import http.handler.EpicsHandler;
import http.handler.HistoryHandler;
import http.handler.PrioritizedHandler;
import http.handler.SubtasksHandler;
import http.handler.TasksHandler;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));

    }

    public TaskManager getManager() {
        return manager;
    }

    public void startServer() {
        System.out.println("Started UserServer " + PORT);
        System.out.println("http://localhost:" + PORT);
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

}
