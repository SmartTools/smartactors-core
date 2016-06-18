package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.ifilesystem_tracker.exception.FilesystemTrackerStartupException;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implementation of {@link IFilesystemTracker}.
 */
public class FilesystemTracker implements IFilesystemTracker {
    private Thread watchThread;
    private Set<IAction<File>> handlers = new CopyOnWriteArraySet<>();
    private Set<IAction<Throwable>> errorHandlers = new CopyOnWriteArraySet<>();

    private final Set<File> knownFiles = new HashSet<>();
    private final Object knownFilesLock = new Object();
    private boolean started = false;

    private FilenameFilter filter;
    private ListeningTaskFactory taskFactory;

    /**
     * The action executed by {@link ListenerTask} to notify {@code FilesystemTracker} about new file.
     */
    private class FileCreationHandler implements IAction<File> {
        @Override
        public void execute(final File file) throws ActionExecuteException {
            synchronized (knownFilesLock) {
                if (!filter.accept(file.getParentFile(), file.getName())) {
                    return;
                }

                if (knownFiles.contains(file)) {
                    return;
                }

                invokeAllHandlers(file);
                knownFiles.add(file);
            }
        }
    }

    /**
     * The constructor.
     *
     * @param filter a filter that should be used to choose files on which should actions added using {@link
     *               #addFileHandler(IAction)} be executed.
     * @param taskFactory a {@link ListeningTaskFactory} instance that should be used to create a {@link ListenerTask}
     *                    for this {@code FilesystemTracker}.
     * @throws InvalidArgumentException if {@code filter} is {@code null} or {@code taskFactory} is {@code null}
     */
    public FilesystemTracker(final FilenameFilter filter, final ListeningTaskFactory taskFactory)
            throws InvalidArgumentException {
        if (null == filter) {
            throw new InvalidArgumentException("Filter should not be null.");
        }

        if (null == taskFactory) {
            throw new InvalidArgumentException("Task factory should not be null.");
        }

        this.filter = filter;
        this.taskFactory = taskFactory;
    }

    @Override
    public void start(final File directory)
            throws FilesystemTrackerStartupException, InvalidArgumentException {
        if (started) {
            throw new FilesystemTrackerStartupException("Filesystem tracker is already started.");
        }

        if (!directory.isDirectory()) {
            throw new InvalidArgumentException("Given file is not a directory.");
        }

        knownFiles.clear();

        try {
            Runnable listeningTask = taskFactory.createRunnable(directory, new FileCreationHandler());
            watchThread = new Thread(listeningTask);

            watchThread.setDaemon(true);
            watchThread.start();

            started = true;
        } catch (IOException e) {
            throw new FilesystemTrackerStartupException("Could not start filesystem tracker.", e);
        }
    }

    @Override
    public void addFileHandler(final IAction<File> handler) {
        synchronized (knownFilesLock) {
            for (File file : knownFiles) {
                invokeHandler(handler, file);
            }

            handlers.add(handler);
        }
    }

    @Override
    public void removeFileHandler(final IAction<File> handler) {
        handlers.remove(handler);
    }

    @Override
    public void addErrorHandler(final IAction<Throwable> handler) {
        errorHandlers.add(handler);
    }

    private void invokeAllHandlers(final File file) {
        for (IAction<File> handler : handlers) {
            invokeHandler(handler, file);
        }
    }

    private void invokeHandler(final IAction<File> handler, final File file) {
        try {
            handler.execute(file);
        } catch (Throwable e) {
            invokeErrorHandlers(e);
        }
    }

    private void invokeErrorHandlers(final Throwable error) {
        for (IAction<Throwable> errorHandler : errorHandlers) {
            try {
                errorHandler.execute(error);
            } catch (ActionExecuteException | InvalidArgumentException ignore) {
            }
        }
    }
}
