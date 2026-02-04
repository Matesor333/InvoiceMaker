package demo.prorotypeinvocemaker.managers;

import javafx.application.Platform;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileWatcher {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Runnable onUpdate;
    private WatchService watchService;
    private Thread watcherThread;

    public void start(String filePath, Runnable onUpdate) {
        if (running.get()) stop();

        this.onUpdate = onUpdate;
        Path path = Paths.get(filePath).toAbsolutePath();
        Path parentDir = path.getParent();
        String fileName = path.getFileName().toString();

        if (parentDir == null || !Files.exists(parentDir)) {
            System.err.println("Cannot watch: " + filePath + " (Parent directory does not exist)");
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            parentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            running.set(true);

            watcherThread = Thread.ofVirtual().name("file-watcher").start(() -> {
                System.out.println("Started watching file: " + filePath);
                while (running.get()) {
                    try {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path changed = (Path) event.context();
                            if (changed.getFileName().toString().equals(fileName)) {
                                System.out.println("File " + fileName + " changed. Triggering update.");
                                if (this.onUpdate != null) {
                                    Platform.runLater(this.onUpdate);
                                }
                            }
                        }
                        boolean valid = key.reset();
                        if (!valid) break;
                    } catch (InterruptedException | ClosedWatchServiceException e) {
                        break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running.set(false);
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
