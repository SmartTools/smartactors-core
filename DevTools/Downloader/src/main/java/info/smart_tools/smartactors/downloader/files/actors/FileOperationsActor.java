package info.smart_tools.smartactors.downloader.files.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileOperationsActor extends StatelessActor {

    @Executable()
    public boolean createDirectory(final Path directory) {
        try {
            if (null != directory && !directory.toFile().exists()) {
                Files.createDirectories(directory);
            }

            return directory != null && directory.toFile().exists();
        } catch (Exception e) {
            throw new RuntimeException("Could not execute file operation with received file.", e);
        }
    }

    @Executable()
    public Path copyFile(final Path source, final Path target) {
        try {
            if (null != source && source.toFile().exists() && null != target) {
                Files.copy(source, target);
            }

            return target;
        } catch (Exception e) {
            throw new RuntimeException("Could not execute file operation with received file.", e);
        }
    }

    @Executable()
    public Boolean deleteFile(final Path path) {
        try {
            if (path.toFile().exists()) {
                Files.delete(path);
            }

            return !path.toFile().exists();
        } catch (Exception e) {
            throw new RuntimeException("Could not execute file operation with received file.", e);
        }
    }

    @Executable()
    public Boolean deleteDirectoryRecursive(final Path path) {
        try {
            if (path.toFile().exists() && path.toFile().isDirectory()) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

            return !path.toFile().exists();
        } catch (Exception e) {
            throw new RuntimeException("Could not execute file operation with received files.", e);
        }
    }
}
