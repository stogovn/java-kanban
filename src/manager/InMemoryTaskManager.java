package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    //Хранение задач всех типов в коллекциях HashMap:
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    //Методы для получения списка всех задач:
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Методы для получения просмотренных задач:
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Методы для удаления всех задач:
    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    //Методы для получения задач по идентификатору:
    @Override
    public Task getTaskByID(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTaskByID(int subTaskId) {
        Subtask subtask = subtasks.get(subTaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicByID(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    //Методы для создания задач:
    @Override
    public void createTask(Task task) {
        counter++;
        task.setId(counter);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createSubTask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdFromEpic())) {
            counter++;
            subtask.setId(counter);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdFromEpic());
            epic.getIdSubtasks().add(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(epic.getId(), epic);
    }

    //Методы для обновления задач:
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdFromEpic())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getIdFromEpic());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void updateEpicStatus(Epic epic) {
        int countOfNew = 0;
        int countOfDone = 0;
        if (epic.getIdSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Integer idSub : epic.getIdSubtasks()) {
            if (subtasks.get(idSub).getStatus() == Status.NEW) {
                countOfNew++;
            }
            if (subtasks.get(idSub).getStatus() == Status.DONE) {
                countOfDone++;
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
    }

    //Методы для удаления по идентификатору
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getIdFromEpic());
        epic.getIdSubtasks().remove(id);
        updateEpicStatus(epic);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        //Удаляя эпик, удаляются все подзадачи
        Epic epic = epics.get(id);
        for (int idSub : epic.getIdSubtasks()) {
            subtasks.remove(idSub);
            historyManager.remove(idSub);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    //Метод получения списка всех подзадач определенного эпика
    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subList = new ArrayList<>();
        for (Integer idSub : epic.getIdSubtasks()) {
            subList.add(subtasks.get(idSub));
        }
        return subList;
    }
}