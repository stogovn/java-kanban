package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager{
    //Методы для получения списка всех задач:
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Task> getHistory();

    //Методы для удаления всех задач:
    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    //Методы для получения задач по идентификатору:
    Task getTaskByID(int taskId);

    Subtask getSubTaskByID(int subTaskId);

    Epic getEpicByID(int epicId);

    //Методы для создания задач:
    void createTask(Task task);

    void createSubTask(Subtask subtask);

    void createEpic(Epic epic);

    //Методы для обновления задач:
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    //Методы для удаления по идентификатору
    void deleteTask(int id);

    void deleteSubtask(Integer id);

    void deleteEpic(int id);

    //Метод получения спиcка всех подзадач определенного эпика
    List<Subtask> getSubtasksOfEpic(int epicId);
}
