package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    //Проверяем работу метода deleteTasks
    @Test
    public void shouldBeEmptyMapTasksAfterDelete() {
        Task task = new Task("Task", "Description task",
                LocalDateTime.of(2018, Month.MARCH, 20, 20, 20), 15);
        manager.createTask(task);
        manager.deleteTasks();
        assertTrue(manager.getTasks().isEmpty(), "Задачи не удалились");
    }

    //Проверяем работу методу deleteSubtasks и deleteEpics
    @Test
    public void shouldBeEmptyMapSubtasksAfterDelete() {
        Subtask sbt = new Subtask("Subtask", "Description subtask", epic.getId(),
                LocalDateTime.of(2018, Month.MARCH, 21, 20, 20), 45);
        manager.createSubTask(sbt);
        manager.deleteSubTasks();
        assertTrue(manager.getSubtasks().isEmpty(), "Подзадачи не удалились");
        assertTrue(epic.getIdSubtasks().isEmpty(), "Подзадачи не удалились из эпика");
        manager.createSubTask(sbt);
        manager.deleteEpics();
        assertTrue(manager.getEpics().isEmpty(), "Эпики не удалились из менеджера");
        assertTrue(manager.getHistory().isEmpty(), "Эпики не удалились из истории");
        assertTrue(manager.getSubtasks().isEmpty(), "Подзадачи не удалились после удаления эпика");
    }

    //Проверяем работу методу updateTask
    @Test
    public void shouldBeAnotherStatusAfterUpdateTask() {
        Task oldTask = new Task("Task", "Description task",
                LocalDateTime.of(2018, Month.MARCH, 22, 22, 22), 45);
        manager.createTask(oldTask);
        Task newTask1 = new Task(oldTask.getId(), "Task", "Description task", Status.IN_PROGRESS,
                LocalDateTime.of(2018, Month.MARCH, 22, 22, 22), 45);
        manager.updateTask(newTask1);
        assertEquals(Status.IN_PROGRESS, manager.getTasks().getFirst().getStatus(), "Статус не обновился");
    }

    //Проверяем работу методов удаления задач типа Task и Epic
    @Test
    public void shouldBeEmptyMapsAfterDelete() {
        manager.deleteEpic(epic.getId());
        assertTrue(manager.getEpics().isEmpty(), "Эпик не удалился");
        Task task = new Task("Task", "Description task",
                LocalDateTime.of(2018, Month.MARCH, 10, 10, 10), 15);
        manager.createTask(task);
        manager.deleteTask(task.getId());
        assertTrue(manager.getTasks().isEmpty(), "Задача не удалилась");
    }

    //Проверяем работу метода получения подзадач определенного эпика
    @Test
    public void shouldBeGetAllCreatedSubtasks() {
        Subtask s1 = new Subtask("S1", "Description s1", epic.getId(),
                LocalDateTime.of(2018, Month.MARCH, 11, 11, 11), 45);
        Subtask s2 = new Subtask("S2", "Description s2", epic.getId(),
                LocalDateTime.of(2018, Month.MARCH, 12, 11, 11), 45);
        Subtask s3 = new Subtask("S2", "Description s2", epic.getId(),
                LocalDateTime.of(2018, Month.MARCH, 13, 11, 11), 45);
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        manager.createSubTask(s3);
        assertEquals(manager.getSubtasks(), manager.getSubtasksOfEpic(epic.getId()), "Эпик без подзадач");
    }
}
