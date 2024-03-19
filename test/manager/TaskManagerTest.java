package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    Epic epic;
    Subtask subtask;
    File file;

    //проверка, что Subtask нельзя обновить с несуществующим ID и ID несущ. эпика
    @Test
    void shouldNotSubtaskBeEpic() {
        int idNotExistEpic = -2;
        int idNotExistSubtask = -3;
        Subtask epicSubtask = new Subtask(idNotExistSubtask, "Name", "Description",
                Status.DONE, idNotExistEpic, LocalDateTime.now(), 20);
        manager.updateSubtask(epicSubtask);
        assertFalse(epic.getIdSubtasks().contains(epicSubtask.getId()));
        assertFalse(manager.getSubtasks().contains(epicSubtask));

    }

    //проверка, что Subtask нельзя добавить несуществующий Эпик
    @Test
    void shouldNotBeAddEpicInEpic() {
        int idNotExistEpic = -1;
        Subtask subtask = new Subtask("Name", "Description",
                idNotExistEpic, LocalDateTime.now(), 20);
        manager.createSubTask(subtask);
        assertFalse(epic.getIdSubtasks().contains(subtask.getId()), "Эпик добавился сам в себя");
    }

    //проверка, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    void shouldBeAddDifferentTask() {
        Task task = new Task("Name", "Description");
        manager.createTask(task);
        assertNotEquals(epic, task, "Эпик и задача одинаковые");
        assertNotEquals(task, subtask, "Задача и подзадача одинаковые");
        assertEquals(manager.getTaskByID(task.getId()), task, "Найдена другая задача");
        assertEquals(manager.getEpicByID(epic.getId()), epic, "Найден другой эпик");
        assertEquals(manager.getSubTaskByID(subtask.getId()), subtask, "Найдена другая подзадача");
    }

    //проверка, что у эпика, после удаления подзадачи, не остаются id подзадач
    @Test
    void shouldNotBeIdSubtasksAfterDeleteSubtaskOfEpic() {
        int idSubtaskForCheck = subtask.getId();
        manager.deleteSubtask(subtask.getId());
        assertFalse(epic.getIdSubtasks().contains(idSubtaskForCheck), "Эпик не удалил id");
    }
}
