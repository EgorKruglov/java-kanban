package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    CustomLinkedList customList = new CustomLinkedList();

    private Node head;
    private Node tail;

    @Override
    public void remove(int id) { // Удалить из истории задачу
        customList.remove(id);
    }

    @Override
    public void add(Task task) {
        customList.linkLast(task);
    }

    @Override
    public ArrayList getHistory() {
        return customList.getTasks();
    }

    private class CustomLinkedList {
        private final HashMap<Integer, Node> customLinkedList = new HashMap<>(15); // <id задачи, узел с задачей>

        private void linkLast(Task task) { // Добавить хвост
            if (customLinkedList.containsKey(task.getId())) { // Если добавляется уже просмотренная задача
                customLinkedList.remove(task.getId());
            } else if (customLinkedList.size() > 9) { // Если история заполнена
                customLinkedList.remove(head.task.getId());
                Node oldHead = head;
                oldHead.next.prev = null;
                head = oldHead.next;
            }
            Node oldTail = tail; // Добавление хвоста
            Node newNode = new Node(tail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = new Node(null, task, null);
                tail = head;
            } else
                oldTail.next = newNode;
            customLinkedList.put(task.getId(), newNode);
        }

        private void remove(int id) { // Удалить из истории задачу
            if (customLinkedList.containsKey(id)) {
                if (head.task.getId() == id) { // Если в голове
                    customLinkedList.remove(id);
                    Node oldHead = head;
                    head = oldHead.next;
                    head.prev = null;

                } else if (tail.task.getId() == id) { // Если в хвосте
                    customLinkedList.remove(id);
                    Node oldTail = tail;
                    tail = oldTail.prev;
                    tail.next = null;

                } else { // Если в середине
                    Node deletingNode = customLinkedList.get(id);
                    deletingNode.prev.next = deletingNode.next;
                    deletingNode.next.prev = deletingNode.prev;
                    customLinkedList.remove(id);

                }
            }
        }

        private ArrayList<Node> getTasks() {
            return new ArrayList<>(customLinkedList.values());
        }

    }

    private static class Node { // Узел списка
        public Task task;
        public Node next;
        public Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}
