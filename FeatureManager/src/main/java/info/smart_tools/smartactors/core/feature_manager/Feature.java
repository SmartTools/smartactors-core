package info.smart_tools.smartactors.core.feature_manager;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ifeature_manager.IFeature;
import info.smart_tools.smartactors.core.ifeature_manager.exception.FeatureManagementException;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Implementation of {@link IFeature}.
 */
class Feature implements IFeature {
    private Set<String> expectedFileNames;
    private List<IPath> presentFiles;
    private List<IAction<Collection<IPath>>> actions;
    private boolean isListening;
    private boolean isPresent;
    private final Object lock;
    private String name;
    private IAction<IPath> fileHandleAction;
    private IFilesystemTracker filesystemTracker;
    private FileSystem fileSystem = FileSystems.getDefault();

    /**
     * Action to execute when filesystem tracker notifies feature about new files.
     */
    private class FileHandlerAction implements IAction<IPath> {
        @Override
        public void execute(final IPath file)
                throws ActionExecuteException {
            synchronized (Feature.this.lock) {
                String fileName = fileSystem.getPath(file.getPath()).getFileName().toString();

                if (expectedFileNames.contains(fileName)) {
                    presentFiles.add(file);
                }

                if (presentFiles.size() != expectedFileNames.size()) {
                    return;
                }

                Feature.this.isPresent = true;
                filesystemTracker.removeFileHandler(FileHandlerAction.this);
            }

            List<ActionExecuteException> exceptions = new LinkedList<>();

            for (IAction<Collection<IPath>> action : actions) {
                try {
                    action.execute(new ArrayList<>(presentFiles));
                } catch (InvalidArgumentException e) {
                    //TODO: may be need another action
                    throw new ActionExecuteException(e);
                } catch (ActionExecuteException e) {
                    exceptions.add(e);
                }
            }

            if (0 != exceptions.size()) {
                ActionExecuteException exception = new ActionExecuteException(
                        "Error(s) occurred while executing actions specified for feature",
                        exceptions.remove(0));

                for (ActionExecuteException e : exceptions) {
                    exception.addSuppressed(e);
                }

                throw exception;
            }
        }
    }

    /**
     * The constructor.
     *
     * @param name                  name of the feature
     * @param filesystemTracker     {@link IFilesystemTracker} instance to use
     * @throws InvalidArgumentException if {@code name} is {@code null}
     * @throws InvalidArgumentException if {@code filesystemTracker} is {@code null}
     */
    Feature(final String name, final IFilesystemTracker filesystemTracker)
            throws InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Feature name should not be null.");
        }

        if (null == filesystemTracker) {
            throw new InvalidArgumentException("File system tracker should not be null.");
        }

        this.expectedFileNames = new HashSet<>();
        this.presentFiles = new LinkedList<>();
        this.actions = new LinkedList<>();
        this.isListening = false;
        this.isPresent = false;
        this.lock = new Object();
        this.name = name;
        this.fileHandleAction = new FileHandlerAction();
        this.filesystemTracker = filesystemTracker;
    }

    @Override
    public void requireFile(final String fileName)
            throws FeatureManagementException {
        synchronized (this.lock) {
            if (isListening || isPresent) {
                throw new FeatureManagementException("requireFile() called after listening started.");
            }

            expectedFileNames.add(fileName);
        }
    }

    @Override
    public void whenPresent(final IAction<Collection<IPath>> action)
            throws FeatureManagementException {
        synchronized (this.lock) {
            if (isPresent) {
                try {
                    action.execute(presentFiles);
                } catch (ActionExecuteException e) {
                    throw new FeatureManagementException("Error occurred executing action for a feature that already is present:", e);
                } catch (InvalidArgumentException e) {
                    throw new FeatureManagementException("Invalid function argument.", e);
                }
            } else {
                actions.add(action);
            }
        }
    }

    @Override
    public void listen()
            throws FeatureManagementException {
        if (0 == expectedFileNames.size()) {
            throw new FeatureManagementException("No files defined for this feature.");
        }

        synchronized (this.lock) {
            if (this.isListening) {
                throw new FeatureManagementException("listen() called twice.");
            }

            this.filesystemTracker.addFileHandler(this.fileHandleAction);

            this.isListening = true;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}
