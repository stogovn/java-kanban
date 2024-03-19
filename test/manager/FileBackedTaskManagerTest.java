package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

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
        assertEquals(2, loadedManager.getTasks().size(), "Должно быть две задачи");
        assertEquals(1, loadedManager.getEpics().size(), "Должен быть один эпик");
        assertEquals(1, loadedManager.getSubtasks().size(), "Должна быть одна подзадача");
        assertEquals(3, loadedManager.getHistory().size(), "В истории должно быть 3 задачи");
        assertTrue(loadedManager.getHistory().contains(t1), "В истории должна быть задача Task1");
        assertTrue(loadedManager.getHistory().contains(s1), "В истории должна быть подзадача Subtask11");
        assertTrue(loadedManager.getHistory().contains(e1), "В истории должен быть эпик Epic1");
    }

    @Test
    public void shouldNotBeDifferentBetweenSaveAndLoadPrioritizedTasks() {
        Task t1 = new Task("Task1", "Description task1",
                LocalDateTime.of(2024, Month.MARCH, 16, 22, 30), 15);
        manager.createTask(t1);
        Epic e1 = new Epic("Epic1", "Description epic1");
        manager.createEpic(e1);
        Subtask s1 = new Subtask("Subtask1", "Description subtask1", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 17, 21, 30), 15);
        Subtask s2 = new Subtask("Subtask2", "Description subtask2", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 18, 21, 45), 15);
        Subtask s3 = new Subtask("Subtask3", "Description subtask3", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 19, 22, 0), 15);
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        manager.createSubTask(s3);
        //Проверяем, что в приоритетном списке только пять задач
        assertEquals(5, manager.getPrioritizedTasks().size());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        //Проверяем, что после выгрузки кол-во задач не изменилось
        assertEquals(5, loadedManager.getPrioritizedTasks().size());
        //Проверяем, что после выгрузки список до и после идентичны
        assertEquals(manager.getPrioritizedTasks(), loadedManager.getPrioritizedTasks(), "Списка разные");
        //Обновим подзадачу s1 с новым статусом, датой начала и продолжительностью
        Subtask s1New = new Subtask(s1.getId(), "Subtask1", "Description subtask1", Status.DONE,
                e1.getId(), LocalDateTime.of(2023, Month.MARCH, 17, 21, 30), 5);
        loadedManager.updateSubtask(s1New);
        //Проверяем, что после обновления подзадачи старая удалилась из списка
        assertFalse(loadedManager.getPrioritizedTasks().contains(s1), "Подзадачи не должно быть в списке");
        //Проверяем, что после изменения выгруженного списка они отличаются
        assertNotEquals(manager.getPrioritizedTasks(), loadedManager.getPrioritizedTasks(), "Списка одинаковые");

    }
}