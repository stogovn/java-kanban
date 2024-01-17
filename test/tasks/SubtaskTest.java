package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    //проверка, что наследники класса Task равны друг другу, если равен их id
    @Test
    public void shouldBePositiveWhenIdAreEqual() {
        Epic epic1 = new Epic("Name", "Description");
        int idSubtask = 2;
        Subtask subtask1 = new Subtask("Name", "Description", epic1.getId());
        Subtask subtask2 = new Subtask("Name", "Description", epic1.getId());
        subtask1.setId(idSubtask);
        subtask2.setId(idSubtask);
        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");
    }

}