package info.smart_tools.smartactors.feature_generator.files.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateFoldersActor extends StatelessActor {

    private static final String PATH_MAIN = "main";
    private static final String PATH_TEST = "test";
    private static final String PATH_SRC = "src";
    private static final String PATH_JAVA = "java";

    @Executable
    public String createFolders(final String projectPath, final String groupId, final String featureName) {
        Path path = Paths.get(projectPath, featureName);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);

                Path mainSrc = Paths.get(path.toString(), buildSourcePath(PATH_MAIN, groupId, featureName));
                Path testSrc = Paths.get(path.toString(), buildSourcePath(PATH_TEST, groupId, featureName));
                Files.createDirectories(mainSrc);
                Files.createDirectories(testSrc);

                return path.toString();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create path " + path, e);
            }
        } else {
            throw new RuntimeException("Folder \"" + path + "\" already exists");
        }
    }

    private String[] buildSourcePath(final String source, final String groupId, final String featureName) {
        List<String> path = new ArrayList<>();
        path.add(PATH_SRC);
        path.add(source);
        path.add(PATH_JAVA);
        path.addAll(Arrays.asList(groupId.split("\\.")));
        path.add(featureName);

        return path.toArray(new String[0]);
    }
}
