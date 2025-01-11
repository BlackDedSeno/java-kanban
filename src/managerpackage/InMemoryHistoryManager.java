    package managerpackage;



    import tasks.Task;
    import java.util.*;

    public class InMemoryHistoryManager implements HistoryManager<Task> {

        private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

        private Node<Task> head;
        private Node<Task> tail;

        private Node<Task> linkLast(Task task) {
            if (head == null) {
                head = new Node<>(task);
                tail = head;
            } else {
                Node<Task> oldTail = tail;
                tail = new Node<>(task);
                tail.prev = oldTail;
                oldTail.next = tail;
            }

            return tail;
        }

        private List<Task> getTasks() {
            List<Task> tasksList = new ArrayList<>();
            Node<Task> iterator = head;
            for (int i = 0; i < historyMap.size(); i++) {
                tasksList.add(iterator.data);
                iterator = iterator.next;
            }
            return tasksList;
        }

        private void removeNode(Node<Task> node) {
            if (node == null) {
                return;
            }
            if (node.prev != null) {
                node.prev.next = node.next;
            } else { // -  head
                head = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            } else { // -  tail
                tail = node.prev;
            }
        }

        private static class Node<T> {

            private  T data;
            private Node<T> next;
            private Node<T> prev;

            private Node(T data) {
                this.data = data;
                this.next = null;
                this.prev = null;
            }
        }

        @Override
        public List<Task> getHistory() {
            return getTasks();
        }

        @Override
        public void add(Task task) {
            if (task != null){
                remove(task.getId());
            } else {
                System.out.println("Ошибка добавления задачи.");
            }

            historyMap.put(task.getId(), linkLast(task));
        }


        @Override
        public void remove(int id) {
            if (historyMap.containsKey(id)) {
                removeNode(historyMap.get(id));
                historyMap.remove(id);
            }
        }

    }