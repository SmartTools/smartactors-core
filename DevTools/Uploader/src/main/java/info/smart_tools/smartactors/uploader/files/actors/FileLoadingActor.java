package info.smart_tools.smartactors.uploader.files.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileLoadingActor extends StatelessActor {

    private final String extensionsFilter;

    public FileLoadingActor(final String extensionsFilter) {
        this.extensionsFilter = "glob:" + extensionsFilter;
    }

    @Executable
    public List<File> loadFiles(final String folderPath) {
        List<File> files = new ArrayList<>();

        try (Stream<Path> folderPaths = Files.walk(Paths.get(folderPath))) {
            folderPaths
                    .filter(Files::isRegularFile)
                    .filter(this::filterFileExtensions)
                    .forEach(path -> files.add(path.toFile()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load files from " + folderPath, e);
        }

        System.out.println("Found following files: [");
        files.forEach(file -> System.out.println("\t" + file.getName()));
        System.out.println("]\n");

        return files;
    }

    private Boolean filterFileExtensions(final Path path) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(extensionsFilter);
        return matcher.matches(path.getFileName());
    }
}
