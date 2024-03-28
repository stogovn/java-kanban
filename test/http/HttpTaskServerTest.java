package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    HttpTaskServer taskServer;
    TaskManager manager;
    private Epic epic;
    private Task task;
    HttpClient client;
    private Subtask subtask;
    private Gson gson;
    private static final int PORT = 8080;


    @BeforeEach
    void init() throws IOException {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
        gson = Managers.getGson();
        client = HttpClient.newHttpClient();
        epic = new Epic("Test epic name", "Description test epic");
        task = new Task("Test task name", "Description test task",
                LocalDateTime.now(), 15);
        taskServer.startServer();
    }

    @AfterEach
    void tearDown() {
        taskServer.stopServer();
    }

    @Test
    void shouldBeCorrectGetMethodEpicsHistoryAndPrioritized() throws IOException, InterruptedException {
        Type epicType = new TypeToken<Epic>() {
        }.getType();
        Type epicsType = new TypeToken<List<Epic>>() {
        }.getType();
        manager.createEpic(epic);
        //Проверяем получение списка эпиков
        URI urlGetEpic = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlGetEpic)
                .GET()
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());
        final List<Epic> actualEpics = gson.fromJson(responseGet.body(), epicsType);
        assertEquals(manager.getEpics(), actualEpics, "Списки эпиков не совпадают");
        //Проверяем получения эпика по id
        URI urlGetEpicId = URI.create("http://localhost:" + PORT + "/epics/1");
        HttpRequest requestGetId = HttpRequest.newBuilder()
                .uri(urlGetEpicId)
                .GET()
                .build();
        HttpResponse<String> responseGetId = client.send(requestGetId, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetId.statusCode());
        final Epic actualEpic = gson.fromJson(responseGetId.body(), epicType);
        final List<Epic> epicsFromManager = manager.getEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(actualEpic.getName(), epic.getName(), "Некорректное имя эпика");
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2024, Month.MARCH, 20, 21, 30), 15);
        manager.createSubTask(subtask);
        //Проверяем у определенного эпика получаемые подзадачи
        URI urlGetEpicSb = URI.create("http://localhost:" + PORT + "/epics/1/subtasks");
        HttpRequest requestGetEpicSb = HttpRequest.newBuilder()
                .uri(urlGetEpicSb)
                .GET()
                .build();
        HttpResponse<String> responseGetEpicSb = client.send(requestGetEpicSb, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetEpicSb.statusCode());
        final List<Integer> actualEpicSb = gson.fromJson(responseGetEpicSb.body(), new TypeToken<List<Integer>>() {
        }.getType());
        assertEquals(epic.getIdSubtasks(), actualEpicSb, "Список сабтасок не совпадает");
        //Проверяем получение приоритетного списка
        URI urlGetPriority = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest requestPriority = HttpRequest.newBuilder()
                .uri(urlGetPriority)
                .GET()
                .build();
        HttpResponse<String> responseGetPriority = client.send(requestPriority, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetPriority.statusCode());
        final List<Task> actualPriority = gson.fromJson(responseGetPriority.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(manager.getPrioritizedTasks(), actualPriority, "Prioritized списки не совпадают");
    }

    @Test
    void shouldBeCorrectPostEpicMethod() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic);
        URI urlPost = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest requestPostEpic = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> responsePost = client.send(requestPostEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePost.statusCode());
        final Epic actualEpic = gson.fromJson(responsePost.body(), new TypeToken<Epic>() {
        }.getType());
        assertNotNull(manager.getEpics(), "Эпики не возвращаются");
        assertEquals(manager.getEpicByID(1), actualEpic, "Эпики не совпадают");
    }

    @Test
    void shouldBeCorrectPostTaskMethod() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);
        URI urlPost = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest requestPostTask = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> responsePost = client.send(requestPostTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePost.statusCode());
        assertNotNull(manager.getTasks(), "Задачи не возвращаются");
    }

    @Test
    void shouldBeCorrectPostSubtaskMethod() throws IOException, InterruptedException {
        manager.createEpic(epic);
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        String subtaskJson = gson.toJson(subtask);
        URI urlPost = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest requestPostSubtask = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> responsePost = client.send(requestPostSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePost.statusCode());
        assertNotNull(manager.getSubtasks(), "Подзадачи не возвращаются");
        //Проверяем обновление подзадачи
        Subtask newSb = new Subtask(2, "Name", "Description", Status.DONE, epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        String newSubtaskJson = gson.toJson(newSb);
        URI urlPostId = URI.create("http://localhost:" + PORT + "/subtasks/2");
        HttpRequest requestPostSubtaskId = HttpRequest.newBuilder()
                .uri(urlPostId)
                .POST(HttpRequest.BodyPublishers.ofString(newSubtaskJson))
                .build();
        HttpResponse<String> responsePostId = client.send(requestPostSubtaskId, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePostId.statusCode());
        assertEquals(manager.getSubTaskByID(2), newSb, "Подзадачи разные");
    }

    @Test
    void shouldBeCorrectGetIdSubtasks() throws IOException, InterruptedException {
        Type type = new TypeToken<Subtask>() {
        }.getType();
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        manager.createSubTask(subtask);
        URI url = URI.create("http://localhost:" + PORT + "/subtasks/1");
        HttpRequest requestGetId = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> responseGetId = client.send(requestGetId, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetId.statusCode());
        final Subtask actualSubtask = gson.fromJson(responseGetId.body(), type);
        assertEquals(manager.getSubTaskByID(subtask.getId()), actualSubtask, "Подзадачи разные");
    }

    @Test
    void shouldBeCorrectPostIdTask() throws IOException, InterruptedException {
        Task oldTask = new Task("Task Name", "Description task",
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        manager.createTask(oldTask);
        Task newTask = new Task(1, "Task Name", "Description task", Status.DONE,
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        String taskJson = gson.toJson(newTask);
        URI urlPost = URI.create("http://localhost:" + PORT + "/tasks/1");
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePost.statusCode());
        assertEquals(manager.getTaskByID(1), newTask, "Задачи разные");
    }

    @Test
    void shouldBeCorrectGetIdTask() throws IOException, InterruptedException {
        Type type = new TypeToken<Task>() {
        }.getType();
        manager.createTask(task);
        URI url = URI.create("http://localhost:" + PORT + "/tasks/1");
        HttpRequest requestGetId = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> responseGetId = client.send(requestGetId, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetId.statusCode());
        final Task actualTask = gson.fromJson(responseGetId.body(), type);
        assertEquals(manager.getTaskByID(task.getId()), actualTask, "Подзадачи разные");
    }

    @Test
    void shouldBeCorrectDeleteEpicMethod() throws IOException, InterruptedException {
        manager.createEpic(epic);
        URI urlDelete = URI.create("http://localhost:" + PORT + "/epics/1");
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode());
        assertTrue(manager.getEpics().isEmpty(), "Эпики не пустые");
    }

    @Test
    void shouldBeCorrectDeleteTaskMethod() throws IOException, InterruptedException {
        manager.createTask(task);
        URI urlDelete = URI.create("http://localhost:" + PORT + "/tasks/1");
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode());
        assertTrue(manager.getTasks().isEmpty(), "Задачи не пустые");
    }

    @Test
    void shouldBeCorrectDeleteSubTaskMethod() throws IOException, InterruptedException {
        manager.createEpic(epic);
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        manager.createSubTask(subtask);
        URI urlDelete = URI.create("http://localhost:" + PORT + "/subtasks/2");
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(urlDelete)
                .DELETE()
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode());
        assertTrue(manager.getSubtasks().isEmpty(), "Подзадачи не пустые");
    }

    @Test
    void shouldBe406StatusCodeWhenCrossingCreate() throws IOException, InterruptedException {
        manager.createEpic(epic);
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        manager.createSubTask(subtask);
        Subtask checkSubtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        String subtaskJson = gson.toJson(checkSubtask);
        URI urlPost = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responsePost.statusCode());
    }

    @Test
    void shouldBeCorrectHistoryAfterUpdate() throws IOException, InterruptedException {
        manager.createEpic(epic);
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2023, Month.MARCH, 20, 21, 30), 15);
        //Проверяем получение истории
        URI urlGetHistory = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest requestGetHistory = HttpRequest.newBuilder()
                .uri(urlGetHistory)
                .GET()
                .build();
        HttpResponse<String> responseGetHistory = client.send(requestGetHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetHistory.statusCode());
        final List<Task> actualHistoryBefore = gson.fromJson(responseGetHistory.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(manager.getHistory(), actualHistoryBefore, "Истории не совпадают");
        //Обновляем историю и проверяем
        manager.createTask(task);
        assertEquals(200, responseGetHistory.statusCode());
        final List<Task> actualHistoryAfter = gson.fromJson(responseGetHistory.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(manager.getHistory(), actualHistoryAfter, "Истории не совпадают");

    }
}