package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyTasks = new HashMap<>();

    private static class Node {
        Task element;
        Node next;
        Node prev;

        public Node(Node prev, Task data, Node next) {
            this.element = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node head;
    private Node tail;
    private int size = 0;

    public void linkLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        if (node != null) {
            while (node.next != null) {
                tasks.add(node.element);
                node = node.next;
            }
            tasks.add(node.element);
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
        return new ArrayList<>(getTasks());
    }

    public void removeNode(Node node) {
        if (node != null) {
            Node next = node.next;
            Node prev = node.prev;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            size--;
        }
    }
}
