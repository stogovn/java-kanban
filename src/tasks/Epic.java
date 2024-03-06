package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> idSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        idSubtasks = new ArrayList<>();
        super.setTypesTask(TypesTask.EPIC);
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(idSubtasks, epic.idSubtasks);
    }

    @Override
    public String toString() {
        return getId()+","+getType()+","+getName()+","+getStatus()+","+getDescription()+",";
    }

}
