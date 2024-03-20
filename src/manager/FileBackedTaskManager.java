package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static manager.StringFormatter.fromString;
import static manager.StringFormatter.historyFromString;
import static manager.StringFormatter.historyToString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileBacked;
    private static int fileCounter = 0;

    public FileBackedTaskManager(File fileBacked) {
        super();
        this.fileBacked = fileBacked;
    }

    public static void main(String[] args) {
        TaskManager manager = loadFromFile(new File("resources/tasks.csv"));
        Task t1 = new Task("Task1", "Description task1");
        Task t2 = new Task("Task2", "Description task2",
                LocalDateTime.of(2024, Month.MARCH, 14, 21, 45), 15);
        manager.createTask(t1);
        manager.createTask(t2);
        Epic e1 = new Epic("Epic1", "Description epic1");
        manager.createEpic(e1);
        Subtask s1 = new Subtask("Subtask1", "Description subtask1", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 13, 21, 30), 15);
        Subtask s2 = new Subtask("Subtask2", "Description subtask2", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 13, 21, 45), 15);
        Subtask s3 = new Subtask("Subtask3", "Description subtask3", e1.getId(),
                LocalDateTime.of(2024, Month.MARCH, 11, 22, 0), 15);
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        manager.createSubTask(s3);
        //Просматриваем задачи
        manager.getTaskByID(t1.getId());
        manager.getSubTaskByID(s1.getId());
        manager.getTaskByID(t2.getId());
        manager.getEpicByID(e1.getId());
        manager.getSubTaskByID(s2.getId());
        manager.getSubTaskByID(s3.getId());
        System.out.println(manager.getPrioritizedTasks());
        System.out.println(e1.getStartTime());
        System.out.println(e1.getEndTime());
        System.out.println(e1.getDuration());
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubTask(Subtask subtask) {
        super.createSubTask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Task getTaskByID(int taskId) {
        Task task = super.getTaskByID(taskId);
        save();
        return task;
    }

    @Override
    public Subtask getSubTaskByID(int subTaskId) {
        Subtask subtask = super.getSubTaskByID(subTaskId);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicByID(int epicId) {
        Epic epic = super.getEpicByID(epicId);
        save();
        return epic;
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(fileBacked)) {
            fileWriter.write("id,type,name,status,description,date and time,duration,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(subtask.toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            taskManager = new FileBackedTaskManager(file);
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                switch (task.getType()) {
                    case TASK:
                        taskManager.tasks.put(task.getId(), task);
                        taskManager.prioritizedTasks.add(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        taskManager.subtasks.put(subtask.getId(), subtask);
                        taskManager.prioritizedTasks.add(subtask);
                        Epic e = taskManager.epics.get(subtask.getIdFromEpic());
                        e.getIdSubtasks().add(subtask.getId());
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        taskManager.epics.put(epic.getId(), epic);
                        break;
                }
                if (task.getId() > fileCounter) {
                    fileCounter = task.getId();
                }
            }
            taskManager.counter = fileCounter;
            String historyLine;
            if ((historyLine = reader.readLine()) != null) {
                List<Integer> history = historyFromString(historyLine);
                HistoryManager historyManager = taskManager.historyManager;
                for (Integer taskId : history) {
                    if (taskManager.tasks.containsKey(taskId)) {
                        historyManager.add(taskManager.tasks.get(taskId));
                    } else if (taskManager.epics.containsKey(taskId)) {
                        historyManager.add(taskManager.epics.get(taskId));
                    } else {
                        historyManager.add(taskManager.subtasks.get(taskId));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }
        return taskManager;
    }


}
