package info.smart_tools.smartactors.downloader.files;

import info.smart_tools.smartactors.downloader.files.actors.FileOperationsActor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileOperationsActorTests {

    @Test
    public void creatingDirectoryPositiveCase() throws Exception {
        if (Paths.get("test_dir").toFile().exists()) {
            Files.walk(Paths.get("test_dir"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        FileOperationsActor actor = new FileOperationsActor();
        boolean result = actor.createDirectory(Paths.get("test_dir"));
        assertTrue(result);
        Path path = Paths.get("test_dir");
        assertTrue(path.toFile().exists());
        assertTrue(path.toFile().isDirectory());
        if (Paths.get("test_dir").toFile().exists()) {
            Files.walk(Paths.get("test_dir"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
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
        actor.createDirectory(dir);
        fail();
    }

    @Test
    public void copingFilesPositiveCase() throws Exception {
        String filename = "test.txt";
        String filename2 = "test1.txt";
        File test = new File(filename);
        if (!test.exists()) {
            Files.createFile(Paths.get(filename));
        }
        if (new File(filename2).exists()) {
            Files.delete(Paths.get(filename2));
        }
        FileOperationsActor actor = new FileOperationsActor();
        Path result = actor.copyFile(Paths.get(filename), Paths.get(filename2));
        assertTrue(result.toFile().exists());
        if (new File(filename).exists()) {
            Files.delete(Paths.get(filename));
        }
        if (new File(filename2).exists()) {
            Files.delete(Paths.get(filename2));
        }
    }

    @Test
    public void copingFilesNegativeCase() throws Exception {
        String filename2 = "test2.txt";
        if (new File(filename2).exists()) {
            Files.delete(Paths.get(filename2));
        }
        FileOperationsActor actor = new FileOperationsActor();
        Path result = actor.copyFile(null, Paths.get(filename2));
        assertFalse(result.toFile().exists());
    }

    @Test
    public void copingFilesNegativeCaseWithException() throws Exception {
        boolean check = false;
        String filename = "test3.txt";
        String filename2 = "test4.txt";
        File test = new File(filename);
        if (!test.exists()) {
            Files.createFile(Paths.get(filename));
        }
        if (new File(filename2).exists()) {
            Files.delete(Paths.get(filename2));
        }
        FileOperationsActor actor = new FileOperationsActor();
        actor.copyFile(Paths.get(filename), Paths.get(filename2));
        try {
            actor.copyFile(Paths.get(filename), Paths.get(filename2));
        } catch (Exception e) {
            check = true;
        } finally {
            if (new File(filename).exists()) {
                Files.delete(Paths.get(filename));
            }
            if (new File(filename2).exists()) {
                Files.delete(Paths.get(filename2));
            }
        }
        assertTrue(check);
    }

    @Test
    public void deletingFilePositiveCase() throws Exception {
        String filename = "test5.txt";
        File test = new File(filename);
        if (!test.exists()) {
            Files.createFile(Paths.get(filename));
        }
        assertTrue(test.exists());
        FileOperationsActor actor = new FileOperationsActor();
        actor.deleteFile(Paths.get(filename));
        assertFalse(test.exists());
    }

    @Test
    public void deletingFileNegativeCase() throws Exception {
        String filename = "test6.txt";
        FileOperationsActor actor = new FileOperationsActor();
        boolean result = actor.deleteFile(Paths.get(filename));
        assertTrue(result);
    }

    @Test(expected = RuntimeException.class)
    public void deletingFileNegativeCaseWithException() throws Exception {
        FileOperationsActor actor = new FileOperationsActor();
        actor.deleteFile(null);
    }

    @Test
    public void deletingDirRecursivelyPositiveCase() throws Exception {
        String dirName = "test_dir1";
        String dirName2 = "test_dir2";
        String filename = "test7.txt";
        if (Paths.get(dirName).toFile().exists()) {
            Files.walk(Paths.get(dirName))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectory(Paths.get(dirName));
        Files.createDirectory(Paths.get(dirName, dirName2));
        Files.createFile(Paths.get(dirName, filename));
        assertTrue(Paths.get(dirName).toFile().exists());
        assertTrue(Paths.get(dirName, dirName2).toFile().exists());
        assertTrue(Paths.get(dirName, filename).toFile().exists());

        FileOperationsActor actor = new FileOperationsActor();
        actor.deleteDirectoryRecursive(Paths.get(dirName));
        assertFalse(Paths.get(dirName).toFile().exists());
        assertFalse(Paths.get(dirName, dirName2).toFile().exists());
        assertFalse(Paths.get(dirName, filename).toFile().exists());

        if (Paths.get(dirName).toFile().exists()) {
            Files.walk(Paths.get(dirName))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void deletingDirRecursivelyNegativeCase() throws Exception {
        String dirName = "test_dir2";
        FileOperationsActor actor = new FileOperationsActor();
        boolean result = actor.deleteDirectoryRecursive(Paths.get(dirName));
        assertTrue(result);
    }

    @Test(expected = RuntimeException.class)
    public void deletingDirRecursivelyNegativeCaseWithException() throws Exception {
        FileOperationsActor actor = new FileOperationsActor();
        Path path = mock(Path.class);
        actor.deleteDirectoryRecursive(path);
        fail();
    }
}
