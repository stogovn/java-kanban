package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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

}
