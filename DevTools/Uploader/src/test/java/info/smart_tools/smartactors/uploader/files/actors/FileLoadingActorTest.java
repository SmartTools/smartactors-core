package info.smart_tools.smartactors.uploader.files.actors;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileLoadingActorTest {

    @Test
    public void fileLoading__loadFileFromResources() throws Exception {
        List<String> filesToBeLoaded = new ArrayList<String>() {{
            add("sample.txt");
            add("variables.env");
        }};

        String pattern = "*.{txt,env}";

        FileLoadingActor loadingActor = new FileLoadingActor(pattern);

        String path = getClass().getClassLoader().getResource("file_loading/").toURI().getPath();

        List<File> files = loadingActor.loadFiles(path);

        assertFalse(files.isEmpty());
        files.forEach(file -> assertTrue(filesToBeLoaded.contains(file.getName())));
    }
}
