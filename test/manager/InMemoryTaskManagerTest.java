package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        subtask = new Subtask("Name", "Description", epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(20));
        manager.createSubTask(subtask);
    }
    //Проверяем корректность расчёта пересечения интервалов
    @Test
    public void shouldBeCorrectTimeCrossing(){
        Task t1 = new Task("Task1", "Description task1",
                LocalDateTime.of(2024, Month.MARCH,21,10,0), Duration.ofHours(2));
        Task t2 = new Task("Task1", "Description task1",
                LocalDateTime.of(2024, Month.MARCH,21,11,0), Duration.ofMinutes(2));
        manager.createTask(t1);
        assertDoesNotThrow(() -> manager.createTask(t2),"Задачи пересекаются, не должно пройти создание");
        Task t3 = new Task("Task1", "Description task1",
                LocalDateTime.of(2023, Month.MARCH,21,11,0), Duration.ofMinutes(2));
        assertDoesNotThrow(() -> manager.createTask(t3),"Ошибка, задачи не пересекаются");
    }
}