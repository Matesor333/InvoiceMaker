package demo.prorotypeinvocemaker;

public class RefreshManager {
    // A single runnable task that any controller can set or call
    private static Runnable refreshTask;

    public static void setRefreshTask(Runnable task) {
        refreshTask = task;
    }

    public static void triggerRefresh() {
        if (refreshTask != null) {
            refreshTask.run();
        }
    }
}
