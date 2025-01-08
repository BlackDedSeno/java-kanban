package managerPackage;



import tasks.Task;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    private final LinkedList<Task> historyList = new LinkedList<>();
/*int maxSize = 10; */// убираем лимит списка историй
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
        /*if (historyList.size() == maxSize) {
            historyList.remove(0);
        }*/ //убираем проверку списка истррии
        historyList.addFirst(task);
    }
    /*  if (historyList.size() == maxSize) {
            historyList.remove(0);
        }
        historyList.addFirst(task);
    }*/

    @Override
    public void remove(int id){
        if (id != 0){
            historyList.remove(id);
        } else {
            System.out.println("Ошибка удаления задачи из истории");
        }
    }

}