package manager;

import tasks.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int counter = 0;
    //Хранение задач всех типов в коллекциях HashMap:
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Subtask> subtasks;
    HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    //Методы для получения списка всех задач:
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public Collection<Epic> getEpics() {
        return epics.values();
    }

    //Методы для удаления всех задач:
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTasks() {
        subtasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }

    //Методы для получения задач по идентификатору:
    public Task getTaskByID(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubTaskByID(int subTaskId) {
        return subtasks.get(subTaskId);
    }

    public Epic getEpicByID(int epicId) {
        return epics.get(epicId);
    }

    //Методы для создания задач:
    public void addTask(Task task) {
        counter++;
        task.setId(counter);
        tasks.put(task.getId(), task);
    }

    public void addSubTask(Subtask subtask) {
        counter++;
        subtask.setId(counter);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getIdFromEpic());
        epic.getSubtasks().add(subtask);
        updateEpic(epic);
    }

    public void addEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(epic.getId(), epic);
    }

    //Методы для обновления задач:
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getIdFromEpic());
        int index = 0;
        for (Subtask sub : epic.getSubtasks()) {
            if (sub.getId() == subtask.getId()) {
                epic.getSubtasks().set(index, subtask);
            }
            index++;
        }
        updateEpic(epic);
    }

    private void updateEpic(Epic epic) {
        int countOfNew = 0;
        int countOfDone = 0;
        for (Subtask sub : epic.getSubtasks()) {
            if (sub.getStatus() == Status.NEW) {
                countOfNew++;
            }
            if (sub.getStatus() == Status.DONE) {
                countOfDone++;
            }
        }
        int countOfSubtasks = epic.getSubtasks().size();
        if (countOfNew == countOfSubtasks) {
            epic.setStatus(Status.NEW);
        } else if (countOfDone == countOfSubtasks) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epics.put(epic.getId(), epic);
    }

    //Методы для удаления по идентификатору
    public void deleteTask(int id) {
        tasks.remove(id, tasks.get(id));
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id, subtasks.get(id));
    }

    public void deleteEpic(int id) {
        //Удаляя эпик, удалются все подзадачи
        for (Subtask sub : subtasks.values()) {
            if (sub.getIdFromEpic() == id) {
                deleteSubtask(sub.getId());
            }
        }
        epics.remove(id, epics.get(id));
    }

    //Метод получения спиcка всех подзадач определенного эпика
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }
}