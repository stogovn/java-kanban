package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> idSubtasks;
    private LocalDateTime epicEndTime;

    public Epic(String name, String description) {
        super(name, description);
        idSubtasks = new ArrayList<>();
    }

    public TypesTask getType() {
        return TypesTask.EPIC;
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public LocalDateTime getEpicEndTime() {
        return epicEndTime;
    }

    public void setEpicEndTime(LocalDateTime epicEndTime) {
        this.epicEndTime = epicEndTime;
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
        String formattedDateTime = getStartTime() != null ? getStartTime().format(FORMATTER) : "";
        return getId() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + "," +
                formattedDateTime + "," +
                getDuration().toMinutes();
    }

}
