package info.smart_tools.smartactors.feature_generator.files.actors;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TemplateFileCreatorActorTest {

    @Test
    public void templateFileCreator__createFileFromTemplate() throws Exception {
        Path directory = Files.createTempDirectory("test_dir");

        String template = "file";
        Map<String, String> tags = new HashMap<>();
        tags.put("${testName}", "load template and create file");

        TemplateFileCreatorActor actor = new TemplateFileCreatorActor();
        actor.createFileFromTemplate(template, directory.toString(), tags);

        try (
                InputStream is = new FileInputStream(Paths.get(directory.toString(), template).toFile());
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr)
        ) {
            String result = bf.readLine();
            assertEquals("File was created by load template and create file", result);
        }
    }

    @Test
    public void templateFileCreator__createFileWithoutTags() throws Exception {
        Path directory = Files.createTempDirectory("test_dir");

        String template = "file";

        TemplateFileCreatorActor actor = new TemplateFileCreatorActor();
        actor.createFileFromTemplate(template, directory.toString(), null);

        try (
                InputStream is = new FileInputStream(Paths.get(directory.toString(), template).toFile());
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr)
        ) {
            String result = bf.readLine();
            assertEquals("File was created by ${testName}", result);
        }
    }
}
