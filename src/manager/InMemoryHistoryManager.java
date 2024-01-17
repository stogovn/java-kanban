package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private int counter = 0;
    private final List<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        counter++;
        int viewedTasks = 10;
        if (counter <= viewedTasks) {
            historyTasks.add(task);
        } else {
            historyTasks.add(task);
            historyTasks.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTasks);
    }
}
