package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    TaskManager manager;
    Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault();
        task = new Task("Name", "Description");
    }

    //проверка, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void shouldBeAllTasksWhichAdded() {
        Task task = new Task("Name", "Description");
        historyManager.add(task);
        historyManager.add(task);
        for (Task taskInHistory : historyManager.getHistory()) {
            assertEquals(taskInHistory, task, "Задача должна быть в истории");
        }
    }

    //Проверка, что в историю не записывается null
    @Test
    void shouldNotAddTaskInHistoryIfTaskIsNull() {
        int idNotExist = -4;
        historyManager.add(null);
        manager.getTaskByID(idNotExist);
        manager.getEpicByID(idNotExist);
        manager.getSubTaskByID(idNotExist);
        int expectedTasks = 0;
        assertEquals(expectedTasks, historyManager.getHistory().size(), "Null записался в историю просмотров");
    }

    //Проверка удаления элементов из двусвязного списка
    @Test
    void shouldNotBeInListWhenRemoveElementInDifferentCases() {
        Task t1 = new Task("T1", "Description1");
        Task t2 = new Task("T2", "Description2");
        Task t3 = new Task("T3", "Description3");
        Task t4 = new Task("T4", "Description4");
        Task t5 = new Task("T5", "Description5");
        manager.createTask(t1);
        manager.createTask(t2);
        manager.createTask(t3);
        manager.createTask(t4);
        manager.createTask(t5);
        historyManager.add(t1);//1
        historyManager.add(t2);//2
        historyManager.add(t3);//3
        historyManager.add(t4);//4
        historyManager.add(t5);//5
        //Удаляем первый элемент
        historyManager.remove(t1.getId());
        // Проверяем, что элемент удален
        assertNull(searchTask(t1));
        // Проверяем, что новая голова соответствует следующему элементу
        Task newHead = historyManager.getHistory().getFirst();
        assertEquals(t2, newHead);
        //Удаляем элемент из середины
        historyManager.remove(t3.getId());
        // Проверяем, что элемент удален
        assertNull(searchTask(t3));
        // Проверяем, что ссылки соседних элементов обновились корректно
        Task taskBeforeRemoved = historyManager.getHistory().get(0);
        Task taskAfterRemoved = historyManager.getHistory().get(1);
        assertEquals(t2, taskBeforeRemoved);
        assertEquals(t4, taskAfterRemoved);
        //Удаляем последний элемент
        historyManager.remove(t5.getId());
        // Проверяем, что элемент удален
        assertNull(searchTask(t5));
        // Проверяем, что новый хвост соответствует предыдущему элементу
        Task newTail = historyManager.getHistory().getLast();
        assertEquals(t4, newTail);

    }
    //Добавим метод поиска элемента для теста
    public Task searchTask (Task task){
        for(Task el: historyManager.getHistory()){
            if(el.equals(task)){
                return el;
            }
        }
        return null;
    }
}