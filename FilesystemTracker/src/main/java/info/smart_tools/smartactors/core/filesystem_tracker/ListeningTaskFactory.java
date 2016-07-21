package info.smart_tools.smartactors.core.filesystem_tracker;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ipath.IPath;

import java.io.IOException;

/**
 * Interface for a factory that creates a {@link Runnable} execution of which results in execution of given action for
 * every file in given directory and for new files appearing in that directory (until the execution is interrupted).
 */
public interface ListeningTaskFactory {
    /**
     * Create a runnable.
     *
     * @param directory the directory that the {@link Runnable} should listen
     * @param handler the action that should be executed for all files in the directory
     * @return a runnable as described in {@link ListeningTaskFactory}
     * @throws IOException if I/O error occurs during creation of a {@code Runnable}
     * @see ListeningTaskFactory
     */
    Runnable createRunnable(IPath directory, IAction<IPath> handler) throws IOException;
}
