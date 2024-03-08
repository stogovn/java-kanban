package manager;

import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyTasks = new HashMap<>();
    private Node head;
    private Node tail;

    static class Node {
        Task element;
        Node next;
        Node prev;

        public Node(Node prev, Task data, Node next) {
            this.element = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private void linkLast(Task element) {
        final Node newNode = new Node(tail, element, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.element);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyTasks.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
            historyTasks.put(task.getId(), tail);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyTasks.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node next = node.next;
            Node prev = node.prev;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
            }
            historyTasks.remove(node.element.getId());
        }
    }
}
