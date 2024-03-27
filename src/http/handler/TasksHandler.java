package http.handler;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class TasksHandler extends Handler {

    private final Type typeTask = new TypeToken<Task>() {
    }.getType();

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Task task;
            String response;
            final String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            response = gson.toJson(manager.getTaskByID(id));
                            if (response != null) {
                                sendResponse(exchange, response);
                            } else {
                                System.out.println("Задачи с id = " + pathId + "не существует");
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            System.out.println("Получен некорректный id = " + pathId);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else if (Pattern.matches("^/tasks$", path)) {
                        response = gson.toJson(manager.getTasks());
                        sendResponse(exchange, response);
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                    break;
                }

                case "DELETE": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            manager.deleteTask(id);
                            System.out.println("Удалили задачу с id = " + id);
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
                    if (Pattern.matches("^/tasks$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            task = gson.fromJson(body, typeTask);
                            boolean isTaskCreate = manager.createTask(task);
                            if (isTaskCreate) {
                                response = gson.toJson(task);
                                sendResponse(exchange, response);
                            } else {
                                exchange.sendResponseHeaders(406, 0);
                            }
                        } else {
                            exchange.sendResponseHeaders(500, 0);
                        }
                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        String body = readResponse(exchange);
                        if (id != -1) {
                            if (body != null) {
                                task = gson.fromJson(body, typeTask);
                                manager.updateTask(task);
                                response = gson.toJson(task);
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
