package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int counter = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    //Хранение задач всех типов в коллекциях HashMap:
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks.stream().toList());
    }

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
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
            updateEpicStatus(epic);
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(LocalDateTime.MIN);
        }
    }

    @Override
    public void deleteEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
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
        try {
            if (prioritizedTasks.stream().anyMatch(t -> timeCrossing(t, task))) {
                counter--;
                throw new CrossingMatchException("Задачи пересекаются, задайте другое время");
            }

            task.setId(counter);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);

        } catch (CrossingMatchException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createSubTask(Subtask subtask) {
        if (!epics.containsKey(subtask.getIdFromEpic())) {
            return;
        }
        counter++;
        Epic epic = epics.get(subtask.getIdFromEpic());
        try {
            if (prioritizedTasks.stream().anyMatch(t -> timeCrossing(t, subtask))) {
                counter--;
                throw new CrossingMatchException("Подзадачи пересекаются, задайте другое время");
            }
            subtask.setId(counter);
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            epic.getIdSubtasks().add(subtask.getId());
            updateEpicStatus(epic);

            if (subtask.getStartTime().isBefore(epic.getStartTime())) {
                epic.setStartTime(subtask.getStartTime());
            }

            Duration duration = epic.getIdSubtasks().stream()
                    .map(id -> subtasks.get(id).getDuration())
                    .reduce(Duration.ZERO, Duration::plus);
            epic.setDuration(duration);

        } catch (CrossingMatchException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(epic.getId(), epic);
    }

    protected boolean timeCrossing(Task t1, Task t2) {
        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t2.getEndTime().isBefore(t1.getStartTime()) ||
                t1.getEndTime().isEqual(t2.getStartTime()) || t2.getEndTime().isEqual(t1.getStartTime()));
    }

    //Методы для обновления задач:
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdFromEpic())) {
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.remove(subtask);
            prioritizedTasks.add(subtask);
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
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        prioritizedTasks.remove(subtasks.get(id));
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
        return epic.getIdSubtasks().stream()
                .map(subtasks::get)
                .toList();
    }
}