package tasks;

public class Subtask extends Task {
    private final int idFromEpic;

    public Subtask(String name, String description, int idFromEpic) {
        super(name, description);
        super.setStatus(Status.NEW);
        this.idFromEpic = idFromEpic;
    }

    public Subtask(int id, String name, String description, Status status, int idFromEpic) {
        super(id, name, description, status);
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
        return getId()+","+getType()+","+getName()+","+getStatus()+","+getDescription()+","+getIdFromEpic();
    }

}
