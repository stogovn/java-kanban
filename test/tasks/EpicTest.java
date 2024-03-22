package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

class EpicTest {
    //проверка, что наследники класса Task равны друг другу, если равен их id
    @Test
    public void shouldBePositiveWhenIdAreEqual() {
        int idEpic = 3;
        Epic epic1 = new Epic("Name", "Description");
        Epic epic2 = new Epic("Name", "Description");
        epic1.setId(idEpic);
        epic2.setId(idEpic);
        assertEquals(epic1, epic2, "Эпики не совпадают.");
    }

    //Проверка обновления статуса эпика
    @Test
    public void shouldBeCorrectStatusAfterUpdateStatusInSubtasks() throws IOException {
        TaskManager manager = new FileBackedTaskManager(File.createTempFile("tasks", ".csv"));
        Epic epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        Subtask s1 = new Subtask("Name1", "Description1", epic.getId(),
                LocalDateTime.of(2024, Month.MARCH, 10, 21, 30), 15);
        Subtask s2 = new Subtask("Name2", "Description2", epic.getId(),
                LocalDateTime.of(2024, Month.MARCH, 11, 21, 30), 15);
        Subtask s3 = new Subtask("Name3", "Description3", epic.getId(),
                LocalDateTime.of(2024, Month.MARCH, 12, 21, 30), 15);
        Subtask s4 = new Subtask("Name4", "Description4", epic.getId(),
                LocalDateTime.of(2024, Month.MARCH, 13, 21, 30), 15);
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        manager.createSubTask(s3);
        manager.createSubTask(s4);
        //Все подзадачи со статусом NEW
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
        s1.setStatus(Status.DONE);
        manager.updateSubtask(s1);
        //Подзадачи со статусами NEW и DONE.
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
        s2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(s2);
        //Подзадачи со статусом IN_PROGRESS
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
        s1.setStatus(Status.DONE);
        s2.setStatus(Status.DONE);
        s3.setStatus(Status.DONE);
        s4.setStatus(Status.DONE);
        manager.updateSubtask(s1);
        manager.updateSubtask(s2);
        manager.updateSubtask(s3);
        manager.updateSubtask(s4);
        //Все подзадачи со статусом DONE.
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }

    //Проверка расчета времени окончания эпика
    @Test
    public void shouldBeCorrectEndTimeInEpic() throws IOException {
        TaskManager manager = new FileBackedTaskManager(File.createTempFile("tasks", ".csv"));
        Epic epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        //Заранее знаем что окончание эпика будет 22-03-2024 22:44
        LocalDateTime dateTimeCheck = LocalDateTime.of(2024, Month.MARCH, 20, 22, 59);
        Subtask s1 = new Subtask("S1", "Description1", epic.getId(),
                LocalDateTime.of(2024, Month.MARCH, 20, 22, 44), 15);
        Subtask s2 = new Subtask("S2", "Description2", epic.getId(),
                LocalDateTime.of(2019, Month.MARCH, 10, 21, 30), 15);
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        assertEquals(dateTimeCheck, manager.getEpicByID(epic.getId()).getEpicEndTime());
    }
}
