package info.smart_tools.smartactors.feature_generator.files;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FileBuilderTest {

    @Test
    public void fileBuilder__loadTemplateAndCreateFile() throws Exception {
        File file = File.createTempFile("test_file", ".tmp");
        String template = "file__template";
        Map<String, String> tags = new HashMap<>();
        tags.put("${testName}", "load template and create file");

        FileBuilder.createFileFromTemplate(template, file, tags);

        try (
                InputStream is = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr)
        ) {
            String result = bf.readLine();
            assertEquals("File was created by load template and create file", result);
        }
    }
}
