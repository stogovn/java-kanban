package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    TaskManager manager;
    Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault();
        task = new Task("Name", "Description");
    }

    //проверка, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    public void shouldBeAllTasksWhichAdded() {
        Task task = new Task("Name", "Description");
        historyManager.add(task);
        historyManager.add(task);
        for (Task taskInHistory : historyManager.getHistory()) {
            assertEquals(taskInHistory, task, "Задача должна быть в истории");
        }
    }
    
    //Проверка, что в историю не записывается null
    @Test
    void shouldNotAddTaskInHistoryIfTaskIsNull() {
        int idNotExist = -4;
        historyManager.add(null);
        manager.getTaskByID(idNotExist);
        manager.getEpicByID(idNotExist);
        manager.getSubTaskByID(idNotExist);
        int expectedTasks = 0;
        assertEquals(expectedTasks, historyManager.getHistory().size(), "Null записался в историю просмотров");
    }

}