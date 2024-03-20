package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.of(2017, Month.MARCH, 21, 10, 0), 15);
        manager.createSubTask(subtask);
    }

    //Проверяем корректность расчёта пересечения интервалов
    @Test
    public void shouldBeCorrectTimeCrossing() {
        Task t1 = new Task("Task1", "Description task1",
                LocalDateTime.of(2024, Month.MARCH, 21, 10, 0), 15);
        manager.createTask(t1);
        //Создаём задачу t2, которая пересекается с t1
        Task t2 = new Task("Task2", "Description task2",
                LocalDateTime.of(2024, Month.MARCH, 21, 10, 10), 15);
        manager.createTask(t2);
        assertTrue(manager.timeCrossing(t1, t2), "Задачи должны пересекаться");
        assertTrue(manager.timeCrossing(t2, t1), "Задачи должны пересекаться");
        //Создаём задачу t3, которая НЕ пересекается с t1
        Task t3 = new Task("Task3", "Description task3",
                LocalDateTime.of(2023, Month.MARCH, 21, 11, 0), 15);
        manager.createTask(t3);
        assertFalse(manager.timeCrossing(t1, t3), "Задачи не должны пересекаться");
        assertFalse(manager.timeCrossing(t3, t1), "Задачи не должны пересекаться");
        //Проверяем что счётчик не изменился после попытки создания пересекающейся задачи
        assertEquals(4, t3.getId(), "Счётчик увеличился");
    }
}