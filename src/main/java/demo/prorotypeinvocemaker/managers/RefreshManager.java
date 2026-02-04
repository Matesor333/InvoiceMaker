package demo.prorotypeinvocemaker.managers;
import java.util.ArrayList;
import java.util.List;

public class RefreshManager {
    private static final List<Runnable> refreshTasks = new ArrayList<>();

    public static synchronized void addRefreshTask(Runnable task) {
        if (!refreshTasks.contains(task)) {
            refreshTasks.add(task);
        }
    }

    @Deprecated
    public static void setRefreshTask(Runnable task) {
        addRefreshTask(task);
    }

    public static synchronized void triggerRefresh() {
        for (Runnable task : refreshTasks) {
            task.run();
        }
    }
}
