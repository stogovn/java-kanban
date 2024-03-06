package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File fileBacked;

    public FileBackedTaskManager(File fileBacked) {
        super();
        this.fileBacked = fileBacked;
    }

    public static void main(String[] args) {
        TaskManager manager = loadFromFile(new File("resources/tasks.csv"));
        Task t1 = new Task("Task1", "Description task1");
        Task t2 = new Task("Task2", "Description task2");
        manager.createTask(t1);
        manager.createTask(t2);
        Epic e1 = new Epic("Epic1", "Description epic1");
        manager.createEpic(e1);
        Subtask s1 = new Subtask("Subtask1", "Description subtask1", e1.getId());
        Subtask s2 = new Subtask("Subtask2", "Description subtask2", e1.getId());
        Subtask s3 = new Subtask("Subtask3", "Description subtask3", e1.getId());
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        manager.createSubTask(s3);
        Epic e2 = new Epic("Epic2", "Description epic3");
        manager.createEpic(e2);
        //Просматриваем задачи
        manager.getEpicByID(e2.getId());
        manager.getTaskByID(t1.getId());
        manager.getSubTaskByID(s1.getId());
        manager.getTaskByID(t2.getId());
        manager.getEpicByID(e1.getId());
        manager.getSubTaskByID(s2.getId());
        manager.getSubTaskByID(s3.getId());
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
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
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
            fileWriter.write("id,type,name,status,description,epic\n");
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
                        taskManager.createTask(task);
                        break;
                    case SUBTASK:
                        taskManager.createSubTask((Subtask) task);
                        break;
                    case EPIC:
                        taskManager.createEpic((Epic) task);
                        break;
                }

            }
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

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] strings = new String[history.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = String.valueOf(history.get(i).getId());
        }
        return String.join(", ", strings);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            history.add(Integer.parseInt(part.trim()));
        }
        return history;
    }

    private static Task fromString(String value) {
        int epic = 0;
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        if (parts.length == 6) {
            epic = Integer.parseInt(parts[5]);
        }
        switch (type) {
            case "EPIC":
                Epic e = new Epic(name, description);
                e.setId(id);
                e.setStatus(status);
                return e;
            case "SUBTASK":
                return new Subtask(id, name, description, status, epic);
        }
        return new Task(id, name, description, status);
    }
}
