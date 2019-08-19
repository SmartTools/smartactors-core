package info.smart_tools.smartactors.feature_management.directory_watcher_actor;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * The service for watching directory on new file creation
 */
public class ListeningTask implements Runnable {

    private IAction<IPath> onCreationNewFile;
    private WatchService watchService;

    /**
     * Creates instance of {@link ListeningTask} by specific arguments
     * @param watchService the watch service
     * @param watchingDirectory the location of watching directory
     * @param onNewFile the action that will be run on creation new file
     * @throws InitializationException if any errors occurred on create instance of {@link ListeningTask}
     */
    public ListeningTask(final WatchService watchService, final IPath watchingDirectory, final IAction<IPath> onNewFile)
            throws InitializationException {
        try {
            Path nioPath = Paths.get(watchingDirectory.getPath());
            this.onCreationNewFile = onNewFile;
            this.watchService = watchService;
            nioPath.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            throw new InitializationException("Could not create new instance of ListeningTask.", e);
        }
    }

    @Override
    public void run() {
        try (WatchService watcher = this.watchService) {
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        this.onCreationNewFile.execute(
                                new info.smart_tools.smartactors.base.path.Path(((Path) event.context()))
                        );
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | ActionExecutionException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
