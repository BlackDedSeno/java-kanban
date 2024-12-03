package managerPackage;



import tasks.Task;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    private final LinkedList<Task> historyList = new LinkedList<>();
int maxSize = 10;
    @Override
    public ArrayList<Task> getHistory() {
        /*LinkedList<Task> history = historyList;

        for (Task task : history) {
            if (task == null) {
                System.out.println("Объект отсутствует");
            }
        }*/
        return new ArrayList<> (historyList);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            historyList.add(task);
        } else {
            System.out.println("Ошибка добавления задачи в историю");
        }
        if (historyList.size() == maxSize) {
            historyList.remove(0);
        }
        historyList.addFirst(task);
    }
      /*  if (historyList.size() == maxSize) {
            historyList.remove(0);
        }
        historyList.addFirst(task);
    }*/
}