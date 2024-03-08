package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
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
        Subtask s1 = new Subtask("Subtask1","Description subtask1",e1.getId());
        manager.createSubTask(s1);
        manager.getTaskByID(t1.getId());
        manager.getSubTaskByID(s1.getId());
        manager.getEpicByID(e1.getId());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(2, loadedManager.getTasks().size(), "Должно быть две задачи");
        assertEquals(1, loadedManager.getEpics().size(), "Должен быть один эпик");
        assertEquals(1, loadedManager.getSubtasks().size(), "Должна быть одна подзадача");
        assertEquals(3,loadedManager.getHistory().size(), "В истории должно быть 3 задачи");
        assertTrue(loadedManager.getHistory().contains(t1),"В истории должна быть задача Task1");
        assertTrue(loadedManager.getHistory().contains(s1),"В истории должна быть подзадача Subtask11");
        assertTrue(loadedManager.getHistory().contains(e1),"В истории должен быть эпик Epic1");
    }
}