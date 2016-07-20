package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.Files;
import java.util.stream.Stream;

/**
 * Implementation of {@link Runnable} that may be created by {@link ListeningTaskFactory}.
 */
public class ListenerTask implements Runnable {

    private FileSystem fileSystem;
    private WatchService watchService;
    private IAction<IPath> newFileAction;
    private IPath directory;

    /**
     * The constructor.
     *
     * @param directory the directory to listen
     * @param newFileAction action to execute when new file detected
     * @throws IOException when I/O errors happens during initialization
     */
    public ListenerTask(final IPath directory, final IAction<IPath> newFileAction)
            throws IOException {
        this(directory, newFileAction, FileSystems.getDefault());
    }

    /**
     * Special constructor which allows to override file system.
     *
     * @param directory the directory to listen
     * @param newFileAction action to execute when new file detected
     * @param fileSystem filesystem where list dirs and watch files
     * @throws IOException when I/O errors happens during initialization
     */
    ListenerTask(final IPath directory, final IAction<IPath> newFileAction, final FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
        this.directory = directory;
        this.newFileAction = newFileAction;

        Path directoryPath = fileSystem.getPath(directory.getPath());

        watchService = fileSystem.newWatchService();
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
                        Path file = ((Path) event.context());
                        Path dirAndFile = fileSystem.getPath(directory.getPath()).resolve(file);
                        newFileAction.execute(new info.smart_tools.smartactors.core.path.Path(dirAndFile));
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
        try (Stream<Path> files = Files.list(fileSystem.getPath(directory.getPath()))) {
            Iterable<Path> iterableFiles = files::iterator;
            for (Path path : iterableFiles) {
                newFileAction.execute(new info.smart_tools.smartactors.core.path.Path(path));
            }
        } catch (IOException e) {
            throw new ActionExecuteException(e);
        }
    }

}
