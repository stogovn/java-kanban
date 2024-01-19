package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final List<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if(task !=null) {
            historyTasks.add(task);
            if (historyTasks.size() > HISTORY_SIZE) {
                historyTasks.remove(0);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTasks);
    }
}
