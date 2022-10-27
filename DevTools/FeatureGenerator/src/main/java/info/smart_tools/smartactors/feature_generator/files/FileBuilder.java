package info.smart_tools.smartactors.feature_generator.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class FileBuilder {

    private static final String TEMPLATES_DIR = "template_files/";

    public static void createFileFromTemplate(
            final String template, final File target, final Map<String, String> tags
    ) {
        BufferedWriter writer = null;
        InputStream templateStream = FileBuilder.class.getClassLoader().getResourceAsStream(TEMPLATES_DIR + template);
        if (templateStream == null) {
            throw new RuntimeException("Failed to load template " + template);
        }

        try (Scanner scanner = new Scanner(templateStream)) {
            writer = new BufferedWriter(new FileWriter(target));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (tags != null) {
                    for (Map.Entry<String, String> tag : tags.entrySet()) {
                        if (line.contains(tag.getKey())) {
                            line = line.replace(tag.getKey(), tag.getValue());
                        }
                    }
                }
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file from template", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.printf("Could not close write buffer: %s\n", e);
            }
        }
    }
}
