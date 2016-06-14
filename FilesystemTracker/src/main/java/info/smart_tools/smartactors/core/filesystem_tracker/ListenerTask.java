package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;

/**
 * Implementation of {@link Runnable} that may be created by {@link ListeningTaskFactory}.
 */
public class ListenerTask implements Runnable {
    private WatchService watchService;
    private IAction<File> newFileAction;
    private File directory;

    /**
     * The constructor.
     *
     * @param directory the directory to listen
     * @param newFileAction action to execute when new file detected
     * @throws IOException when I/O errors happens during initialization
     */
    public ListenerTask(final File directory, final IAction<File> newFileAction)
            throws IOException {
        this.directory = directory;
        this.newFileAction = newFileAction;

        Path directoryPath = directory.toPath();

        watchService = directoryPath.getFileSystem().newWatchService();
        directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Override
    public void run() {
        try (WatchService watcher = watchService) {
            scanExistFiles();

            for (;;) {
                WatchKey key = watcher.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        File file = ((Path) event.context()).toFile();
                        newFileAction.execute(file);
                    }
                }

                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | ActionExecuteException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanExistFiles()
            throws ActionExecuteException, InvalidArgumentException {
        for (File file : directory.listFiles()) {
            newFileAction.execute(file);
        }
    }
}
