package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    //проверка, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void shouldBePositiveWhenIdAreEqual() {
        int id = 1;
        Task task1 = new Task("Name", "Description");
        task1.setId(id);
        Task task2 = new Task("Name", "Description");
        task2.setId(id);
        assertEquals(task1, task2, "Задачи не совпадают.");
    }

}