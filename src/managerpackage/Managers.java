package managerpackage;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}