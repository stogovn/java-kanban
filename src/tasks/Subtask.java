package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int idFromEpic;

    public Subtask(String name, String description, int idFromEpic, LocalDateTime startTime, long duration) {
        super(name, description, startTime, duration);
        super.setStatus(Status.NEW);
        this.idFromEpic = idFromEpic;
    }

    public Subtask(int id, String name, String description, Status status,
                   int idFromEpic, LocalDateTime startTime, long duration) {
        super(id, name, description, status, startTime, duration);
        this.idFromEpic = idFromEpic;
    }
    public Subtask(int id, String name, String description, Status status, int idFromEpic) {
        super(name, description);
        super.setId(id);
        super.setStatus(status);
        this.idFromEpic = idFromEpic;
    }

    public TypesTask getType() {
        return TypesTask.SUBTASK;
    }

    public int getIdFromEpic() {
        return idFromEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idFromEpic == subtask.idFromEpic;
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
                getDuration().toMinutes() + "," +
                getIdFromEpic();
    }

}
