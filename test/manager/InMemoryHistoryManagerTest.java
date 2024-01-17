package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
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

    //Проверка, что в историю записывается только 10 задач
    @Test
    public void shouldBe10TasksInHistory() {
        int expectedTasks = 10;
        for (int i = 0; i <= 13; i++) {
            Task task = new Task("Name", "Description");
            historyManager.add(task);
        }
        assertEquals(expectedTasks,historyManager.getHistory().size(),"Задач больше 10");
    }

}