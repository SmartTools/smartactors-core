package info.smart_tools.smartactors.core.path;

import info.smart_tools.smartactors.core.ipath.IPath;

import java.io.File;

/**
 * Implementation of IPath.
 * Allows to create paths from String, {@link java.io.File} and {@link java.nio.file.Path}.
 * Has correct implementations of equals() and hashCode().
 */
public class Path implements IPath {

    private final String path;

    /**
     * Creates the path from String.
     * @param path  string representation of the path
     */
    public Path(final String path) {
        this.path = path;
    }

    /**
     * Creates the path from File.
     * @param file  path as java.io.File
     */
    public Path(final File file) {
        this.path = file.getPath();
    }

    /**
     * Creates the path from java.nio.file.Path.
     * @param nioPath   path as java.nio.file.Path
     */
    public Path(final java.nio.file.Path nioPath) {
        this.path = nioPath.toString();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Path path1 = (Path) o;
        return path != null ? path.equals(path1.path) : path1.path == null;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public String toString() {
        return this.path;
    }

}
