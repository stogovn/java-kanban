package http.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Epic;

import java.lang.reflect.Type;
import java.util.regex.Pattern;


public class EpicsHandler extends Handler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;
    private final Type typeEpic = new TypeToken<Epic>() {
    }.getType();

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Epic epic;
            String response;
            final String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            response = gson.toJson(manager.getEpicByID(id));
                            if (response != null) {
                                sendResponse(exchange, response);
                            } else {
                                System.out.println("Эпика с id = " + pathId + "не существует");
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else if (Pattern.matches("^/epics$", path)) {
                        response = gson.toJson(manager.getEpics());
                        sendResponse(exchange, response);
                    } else if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/epics/", "")
                                .replaceFirst("/subtasks", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            response = gson.toJson(manager.getEpicByID(id).getIdSubtasks());
                            if (response != null) {
                                sendResponse(exchange, response);
                            } else {
                                System.out.println("Эпика с id = " + pathId + "не существует");
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                    break;
                }

                case "DELETE": {
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            manager.deleteEpic(id);
                            System.out.println("Удалили эпик с id = " + id);
                            exchange.sendResponseHeaders(200, 0);
                            exchange.close();
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/epics$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            epic = gson.fromJson(body, typeEpic);
                            manager.createEpic(epic);
                            response = gson.toJson(epic);
                            sendResponse(exchange, response);
                        } else {
                            exchange.sendResponseHeaders(500, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                    break;
                }
                default: {
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
