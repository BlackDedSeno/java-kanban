package manager_package;


import java.util.List;

public interface HistoryManager<T> {

    List<T> getHistory();

    void add(T task);

    void remove(int id);
}





