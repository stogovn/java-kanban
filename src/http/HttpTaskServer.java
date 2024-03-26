package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpServer;
import http.adapter.LocalDateTimeAdapter;
import http.adapter.TaskAdapter;
import http.handler.EpicsHandler;

import http.handler.HistoryHandler;
import http.handler.PrioritizedHandler;
import http.handler.SubtasksHandler;
import http.handler.TasksHandler;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson;
    private final TaskManager manager;
    private final HttpServer httpServer;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gson = gsonBuilder
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public HttpTaskServer() throws IOException {
        this.manager = Managers.getDefault();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));

    }

    public static Gson getGson() {
        return gson;
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
