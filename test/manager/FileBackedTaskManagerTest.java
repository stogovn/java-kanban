package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tmpFile;

    @BeforeEach
    void setUp() {
        try {
            tmpFile = File.createTempFile("tasks", ".csv");
            manager = new FileBackedTaskManager(tmpFile);
            epic = new Epic("Name", "Description");
            manager.createEpic(epic);
            subtask = new Subtask("Name", "Description", epic.getId(),
                    LocalDateTime.of(2024, Month.MARCH, 20, 21, 30), 15);
            manager.createSubTask(subtask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldBeEmptyTaskManagerAfterSaveAndLoad() throws IOException {
        tmpFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tmpFile);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertTrue(loadedManager.getTasks().isEmpty(), "Задач не должно быть в менеджере");
        assertTrue(loadedManager.getEpics().isEmpty(), "Эпиков не должно быть в менеджере");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Подзадач не должно быть в менеджере");
        assertTrue(loadedManager.getHistory().isEmpty(), "История должна быть пустой");
        assertTrue(loadedManager.getPrioritizedTasks().isEmpty(), "Список приоритетных задач не пустой");
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tmpFile));
    }

    @Test
    void shouldBeAllTasksInTaskManagerAfterSaveAndLoadMultipleTasks() throws IOException {
        tmpFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tmpFile);
        Task t1 = new Task("Task1", "Description task1",
                LocalDateTime.of(2023, Month.MARCH, 21, 21, 30), 15);
        Task t2 = new Task("Task2", "Description task2",
                LocalDateTime.of(2024, Month.MARCH, 22, 21, 30), 15);
        manager.createTask(t1);
        manager.createTask(t2);
        Epic e1 = new Epic("Epic1", "Description epic1");
        manager.createEpic(e1);
        Subtask s1 = new Subtask("Subtask1", "Description subtask1", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 20, 21, 30), 15);
        manager.createSubTask(s1);
        manager.getTaskByID(t1.getId());
        manager.getSubTaskByID(s1.getId());
        manager.getEpicByID(e1.getId());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Списки задач не идентичны");
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки эпиков не идентичны");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Списки подзадач не идентичны");
        assertEquals(manager.getHistory(), loadedManager.getHistory(), "Истории не идентичны");
        assertEquals(manager.getPrioritizedTasks(),
                loadedManager.getPrioritizedTasks(), "Список приоритетных задач разные");
    }
}