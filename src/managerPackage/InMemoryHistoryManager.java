package managerPackage;



import tasks.Task;

import java.util.ArrayList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    private final List<Task> historyList = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        historyList.addFirst(task);
    }

}