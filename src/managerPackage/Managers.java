package managerPackage;

public class Managers {

    private static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}