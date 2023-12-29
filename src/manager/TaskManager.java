package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int counter = 0;
    //Хранение задач всех типов в коллекциях HashMap:
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    //Методы для получения списка всех задач:
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Методы для удаления всех задач:
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTasks() {
        subtasks.clear();
        for(Epic epic:epics.values()){
            epic.getIdSubtasks().clear();
            autoUpdateEpic(epic);
        }
    }

    public void deleteEpics() {
        epics.clear();
        deleteSubTasks();
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
        epic.getIdSubtasks().add(subtask.getId());
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
        autoUpdateEpic(epic);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void autoUpdateEpic(Epic epic) {
        int countOfNew = 0;
        int countOfDone = 0;
        if (epic.getIdSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer idSub : epic.getIdSubtasks()) {
                if (getSubTaskByID(idSub).getStatus() == Status.NEW) {
                    countOfNew++;
                }
                if (getSubTaskByID(idSub).getStatus() == Status.DONE) {
                    countOfDone++;
                }
            }
        }
        int countOfSubtasks = epic.getIdSubtasks().size();
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
        tasks.remove(id);
    }

    public void deleteSubtask(Integer id) {
        Subtask subtask = getSubTaskByID(id);
        Epic epic = getEpicByID(subtask.getIdFromEpic());
        epic.getIdSubtasks().remove(id);
        autoUpdateEpic(epic);
        subtasks.remove(id);
    }

    public void deleteEpic(int id) {
        //Удаляя эпик, удалются все подзадачи
        Epic epic = getEpicByID(id);
        for (int idSub: epic.getIdSubtasks()) {
            subtasks.remove(idSub);
        }
        epics.remove(id);
    }

    //Метод получения спиcка всех подзадач определенного эпика
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subList = new ArrayList<>();
        for (Integer idSub : epic.getIdSubtasks()) {
            subList.add(getSubTaskByID(idSub));
        }
        return subList;
    }
}