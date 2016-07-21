package info.smart_tools.smartactors.core.ifilesystem_tracker;

import info.smart_tools.smartactors.core.ifilesystem_tracker.exception.FilesystemTrackerStartupException;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ipath.IPath;

/**
 * Interface for a service that notifies observers (executing {@link IAction}s) about all files exist and appearing in
 * given directory.
 */
public interface IFilesystemTracker {
    /**
     * Notify observers about a all exist files in given directory and start listen for new files.
     *
     * @param directory a directory to listen
     * @throws FilesystemTrackerStartupException when this tracker is already started
     * @throws FilesystemTrackerStartupException if the tracker could not start
     * @throws InvalidArgumentException if given file is not a directory
     */
    void start(IPath directory) throws FilesystemTrackerStartupException, InvalidArgumentException;

    /**
     * Add action that should be executed when new file appears in observable directory. When there already are files in
     * observable directory and the {@code IFilesystemTracker} is started the action will be executed immediately on
     * that files.
     *
     * @param action the action that should be executed for all exist files and files that will appear in future
     */
    void addFileHandler(IAction<IPath> action);

    /**
     * Remove a action added using {@link #addFileHandler(IAction)}.
     *
     * @param action the action that should no more be executed for new files.
     */
    void removeFileHandler(IAction<IPath> action);

    /**
     * Add action that should be executed when any error happens in any of actions added using {@link
     * #addFileHandler(IAction)}.
     *
     * @param action the action to execute in case of error
     */
    void addErrorHandler(IAction<Throwable> action);
}
