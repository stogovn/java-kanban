package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int counter = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    //Хранение задач всех типов в коллекциях HashMap:
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    private static final Comparator<Task> taskComparator = Comparator.nullsLast(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

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
            prioritizedTasks.removeIf(oldSubtask -> oldSubtask.getId() == subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
            updateEpicStatus(epic);
            updateEpicTimeInformation(epic);
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
        if (prioritizedTasks.stream().anyMatch(t -> timeCrossing(t, task))) {
            System.out.println("Задачи пересекаются, задайте другое время");
            return;
        }
        counter++;
        task.setId(counter);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createSubTask(Subtask subtask) {
        if (!epics.containsKey(subtask.getIdFromEpic())) {
            return;
        }
        Epic epic = epics.get(subtask.getIdFromEpic());
        if (prioritizedTasks.stream().anyMatch(t -> timeCrossing(t, subtask))) {
            System.out.println("Подзадачи пересекаются, задайте другое время");
            return;
        }
        counter++;
        subtask.setId(counter);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        epic.getIdSubtasks().add(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTimeInformation(epic);
    }

    @Override
    public void createEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(epic.getId(), epic);
    }

    protected boolean timeCrossing(Task existTask, Task newTask) {
        if (existTask.getStartTime() == null || newTask.getStartTime() == null
                || existTask.getEndTime() == null || newTask.getEndTime() == null) {
            return false;
        } else {
            return existTask.getStartTime().isBefore(newTask.getEndTime())
                    && !existTask.getStartTime().isEqual(newTask.getEndTime())
                    && newTask.getStartTime().isBefore(existTask.getEndTime())
                    && !newTask.getStartTime().isEqual(existTask.getEndTime());
        }
    }

    //Методы для обновления задач:
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.removeIf(oldTask -> oldTask.getId() == task.getId());
        prioritizedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getIdFromEpic())) {
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.removeIf(oldSubtask -> oldSubtask.getId() == subtask.getId());
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(subtask.getIdFromEpic());
            updateEpicStatus(epic);
            updateEpicTimeInformation(epic);
        }
    }

    private void updateEpicTimeInformation(Epic epic) {
        Optional<Subtask> earliestSubtask = epic.getIdSubtasks().stream()
                .map(subtasks::get)
                .min(taskComparator);
        earliestSubtask.ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));
        Optional<Subtask> latestSubtask = epic.getIdSubtasks().stream()
                .map(subtasks::get)
                .max(taskComparator);
        latestSubtask.ifPresent(subtask -> epic.setEpicEndTime(subtask.getEndTime()));
        long duration = epic.getIdSubtasks().stream()
                .map(id -> subtasks.get(id).getDuration())
                .reduce(0L, Long::sum);
        epic.setDuration(duration);
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
        prioritizedTasks.removeIf(oldTask -> oldTask.getId() == id);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        prioritizedTasks.removeIf(oldSubtask -> oldSubtask.getId() == id);
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getIdFromEpic());
        epic.getIdSubtasks().remove(id);
        updateEpicStatus(epic);
        updateEpicTimeInformation(epic);
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