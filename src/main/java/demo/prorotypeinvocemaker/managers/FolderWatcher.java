package demo.prorotypeinvocemaker.managers;


import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FolderWatcher {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private Runnable onUpdate;
    private WatchService watchService;
    private Thread watcherThread;

    public void start(String folderPath, Runnable onUpdate) {
        if (running.get()) stop(); // Stop existing if any

        this.onUpdate = onUpdate;
        Path path = Paths.get(folderPath);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            System.err.println("Cannot watch: " + folderPath + " (Not a folder)");
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            running.set(true);

            // Create and start a Virtual Thread
            watcherThread = Thread.ofVirtual().name("folder-watcher").start(() -> {
                System.out.println("Started watching: " + folderPath);

                while (running.get()) {
                    try {
                        WatchKey key = watchService.take(); // Blocks until event occurs

                        // Events happened!
                        for (WatchEvent<?> event : key.pollEvents()) {
                            System.out.println("File event: " + event.kind() + " - " + event.context());
                        }

                        // Trigger UI refresh safely
                        if (onUpdate != null) {
                            Platform.runLater(onUpdate);
                        }

                        boolean valid = key.reset();
                        if (!valid) {
                            System.out.println("WatchKey no longer valid. Stopping.");
                            break;
                        }

                    } catch (InterruptedException e) {
                        System.out.println("Watcher thread interrupted.");
                        break;
                    } catch (ClosedWatchServiceException e) {
                        break; // Service closed, exit loop
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
                watchService.close(); // This breaks the 'take()' block
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

