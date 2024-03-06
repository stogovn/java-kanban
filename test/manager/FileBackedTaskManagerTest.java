package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tmpFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void beforeEach() {
        try {
            tmpFile = File.createTempFile("tasks", ".csv");
            manager = new FileBackedTaskManager(tmpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void deleteFile() {
        tmpFile.delete();
    }

    @Test
    void shouldBeEmptyTaskManagerAfterSaveAndLoad() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertTrue(loadedManager.getTasks().isEmpty(), "Задач не должно быть в менеджере");
        assertTrue(loadedManager.getEpics().isEmpty(), "Эпиков не должно быть в менеджере");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Подзадач не должно быть в менеджере");
        assertTrue(loadedManager.getHistory().isEmpty(), "История должна быть пустой");
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tmpFile));
    }

    @Test
    void shouldBeAllTasksInTaskManagerAfterSaveAndLoadMultipleTasks() {
        Task t1 = new Task("Task1", "Description task1");
        Task t2 = new Task("Task2", "Description task2");
        manager.createTask(t1);
        manager.createTask(t2);
        Epic e1 = new Epic("Epic1", "Description epic1");
        manager.createEpic(e1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(2, loadedManager.getTasks().size(), "Должно быть две задачи");
        assertEquals(1, loadedManager.getEpics().size(), "Должен быть один эпик");
    }
}