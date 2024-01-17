package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    //проверка, что Subtask нельзя обновить с несуществующим ID и ID несущ. эпика
    @Test
    void shouldNotSubtaskBeEpic() {
        int idNotExistEpic = -2;
        int idNotExistSubtask = -3;
        Epic epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Name", "Description", epic.getId());
        manager.createSubTask(subtask);
        Subtask epicSubtask = new Subtask(idNotExistSubtask, "Name", "Description", Status.DONE, idNotExistEpic);
        manager.updateSubtask(epicSubtask);
        assertFalse(epic.getIdSubtasks().contains(epicSubtask.getId()));
        assertFalse(manager.getSubtasks().contains(epicSubtask));

    }

    //проверка, что Subtask нельзя добавить несуществующий Эпик
    @Test
    void shouldNotBeAddEpicInEpic() {
        Epic epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        int idNotExistEpic = -1;
        Subtask subtask = new Subtask("Name", "Description", idNotExistEpic);
        manager.createSubTask(subtask);
        assertFalse(epic.getIdSubtasks().contains(subtask.getId()), "Эпик добавился сам в себя");
    }

    //проверка, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    void shouldBeAddDifferentTask() {
        Epic epic = new Epic("Name", "Description");
        manager.createEpic(epic);
        Task task = new Task("Name", "Description");
        manager.createTask(task);
        Subtask subtask = new Subtask("Name", "Description", epic.getId());
        manager.createSubTask(subtask);
        assertNotEquals(epic, task, "Эпик и задача одинаковые");
        assertNotEquals(task, subtask, "Задача и подзадача одинаковые");
        assertEquals(manager.getTaskByID(task.getId()), task, "Найдена другая задача");
        assertEquals(manager.getEpicByID(epic.getId()), epic, "Найден другой эпик");
        assertEquals(manager.getSubTaskByID(subtask.getId()), subtask, "Найдена другая подзадача");
    }
}