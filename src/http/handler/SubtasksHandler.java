package http.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class SubtasksHandler extends Handler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;
    private final Type typeSubtask = new TypeToken<Subtask>() {
    }.getType();

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Subtask subtask;
            String response;
            final String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            response = gson.toJson(manager.getSubTaskByID(id));
                            if (response != null) {
                                sendResponse(exchange, response);
                            } else {
                                System.out.println("Подзадачи с id = " + pathId + "не существует");
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else if (Pattern.matches("^/subtasks$", path)) {
                        response = gson.toJson(manager.getSubtasks());
                        sendResponse(exchange, response);
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                    break;
                }

                case "DELETE": {
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            manager.deleteSubtask(id);
                            System.out.println("Удалили подзадачу с id = " + id);
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
                    if (Pattern.matches("^/subtasks$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            subtask = gson.fromJson(body, typeSubtask);
                            boolean isSubtaskCreate = manager.createSubTask(subtask);
                            if (isSubtaskCreate) {
                                response = gson.toJson(subtask);
                                sendResponse(exchange, response);
                            } else {
                                exchange.sendResponseHeaders(406, 0);
                            }
                        } else {
                            exchange.sendResponseHeaders(500, 0);
                        }
                    } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        String body = readResponse(exchange);
                        if (id != -1) {
                            if (body != null) {
                                subtask = gson.fromJson(body, typeSubtask);
                                manager.updateSubtask(subtask);
                                response = gson.toJson(subtask);
                                sendResponse(exchange, response);
                            } else {
                                exchange.sendResponseHeaders(500, 0);
                            }
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                }
                break;
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
