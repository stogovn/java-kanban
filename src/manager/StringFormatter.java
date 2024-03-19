package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import tasks.TypesTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StringFormatter {
    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] strings = new String[history.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = String.valueOf(history.get(i).getId());
        }
        return String.join(", ", strings);
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            history.add(Integer.parseInt(part.trim()));
        }
        return history;
    }

    static Task fromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        int epic = -1;
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TypesTask type = TypesTask.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        if (parts.length == 8) {
            epic = Integer.parseInt(parts[7]);
        }
        LocalDateTime startTime = null;
        if (!parts[5].isEmpty()) {
            startTime = LocalDateTime.parse(parts[5], formatter);
        }
        long duration = Long.parseLong(parts[6]);
        switch (type) {
            case EPIC:
                Epic epicFromString = new Epic(name, description);
                epicFromString.setId(id);
                epicFromString.setStatus(status);
                if (startTime != null) {
                    epicFromString.setStartTime(startTime);
                }
                epicFromString.setDuration(duration);
                return epicFromString;
            case SUBTASK:
                Subtask subtaskFromString = new Subtask(id, name, description, status, epic);
                if (startTime != null) {
                    subtaskFromString.setStartTime(startTime);
                }
                subtaskFromString.setDuration(duration);
                return subtaskFromString;
        }
        Task taskFromString = new Task(name, description);
        taskFromString.setId(id);
        taskFromString.setStatus(status);
        if (startTime != null) {
            taskFromString.setStartTime(startTime);
        }
        taskFromString.setDuration(duration);
        return taskFromString;
    }
}
