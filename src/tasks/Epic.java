package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                '\'' + getName() + '\'' +
                ", ID=" + getId() +
                ", " + getStatus() +
                '}';
    }

}
