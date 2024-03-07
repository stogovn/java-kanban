package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import tasks.TypesTask;

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
        int epic = 0;
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TypesTask type = TypesTask.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        if (parts.length == 6) {
            epic = Integer.parseInt(parts[5]);
        }
        switch (type) {
            case EPIC:
                Epic e = new Epic(name, description);
                e.setId(id);
                e.setStatus(status);
                return e;
            case SUBTASK:
                return new Subtask(id, name, description, status, epic);
        }
        return new Task(id, name, description, status);
    }
}
