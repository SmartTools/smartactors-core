package info.smartactors.downloader.files;

import info.smart_tools.smartactors.downloader.files.actors.FileOperationsActor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileOperationsActorTests {

    @Before
    public void init() throws Exception {
        if (Paths.get("test_dir").toFile().exists()) {
            Files.walk(Paths.get("test_dir"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void creatingDirectoryPositiveCase() {
        FileOperationsActor actor = new FileOperationsActor();
        boolean result = actor.createDirectory(Paths.get("test_dir"));
        assertTrue(result);
        Path path = Paths.get("test_dir");
        assertTrue(path.toFile().exists());
        assertTrue(path.toFile().isDirectory());
    }

    @Test
    public void creatingDirectoryNegativeCase() {
        FileOperationsActor actor = new FileOperationsActor();
        boolean result = actor.createDirectory(null);
        assertFalse(result);
    }

    @Test(expected = Exception.class)
    public void creatingDirectoryNegativeCaseWithException() {
        FileOperationsActor actor = new FileOperationsActor();
        Path dir = mock(Path.class);
        when(dir.toFile()).thenThrow(new Exception());
        actor.createDirectory(dir);
        fail();
    }
}
