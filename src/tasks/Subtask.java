package tasks;

public class Subtask extends Task {
    private final int idFromEpic;

    public Subtask(String name, String description, Status status, int idFromEpic) {
        super(name, description, status);
        this.idFromEpic = idFromEpic;
    }

    public Subtask(int id, String name, String description, Status status, int idFromEpic) {
        super(id, name, description, status);
        this.idFromEpic = idFromEpic;
    }

    public int getIdFromEpic() {
        return idFromEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                '\'' + getName() + '\'' +
                ", ID=" + getId() +
                ", EpicID=" + getIdFromEpic() +
                ", " + getStatus() +
                '}';
    }

}
