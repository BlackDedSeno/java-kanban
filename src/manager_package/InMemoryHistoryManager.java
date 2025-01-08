    package manager_package;



    import tasks.Task;

    import java.util.*;

    public class InMemoryHistoryManager implements HistoryManager<Task> {
       /* private final LinkedList<Task> historyList = new LinkedList<>();*/ //список истории через список
        private final Map <Integer, Node<Task>> historyMap = new HashMap<>();

        /*int maxSize = 10; */// убираем лимит списка историй

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


        @Override
        public List<Task> getHistory() {
        /*return new ArrayList<>(historyList);*/ //старая реализация метода
            return getTasks();

        }

        @Override
        public void add(Task task) {
            /*if (task != null) {
                historyList.add(task);
            } else {
                System.out.println("Ошибка добавления задачи в историю");
            }
            *//*if (historyList.size() == maxSize) {
                historyList.remove(0);
            }*//* //убираем проверку списка истррии
            historyList.addFirst(task);*/ //старая реализация метода
            remove(task.getId());
            historyMap.put(task.getId(), linkLast(task));
        }


        @Override
        public void remove(int id){
            if (historyMap.containsKey(id)) {
                removeNode(historyMap.get(id));
                historyMap.remove(id);
            }
        }

    }