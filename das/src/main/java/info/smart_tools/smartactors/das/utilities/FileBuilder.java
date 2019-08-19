package info.smart_tools.smartactors.das.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public final class FileBuilder {

    private static final String RESOURCE_DIR = "resources";

    private FileBuilder() {
    }

    public static void createFileByTemplateWithReplace(
            final File sourceTemplate, final File target, final Map<String, String> tags
    ) {
        BufferedWriter writer = null;
        InputStream templateStream;
        try {
            templateStream = new FileInputStream(
                    TemplatePathBuilder.buildTemplatePath(sourceTemplate.getName())
            );
        } catch (IOException e) {
            System.out.println("Could not open template file");

            return;
        }
        try (Scanner scanner = new Scanner(templateStream)) {
            writer = new BufferedWriter(new FileWriter(target));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    if (line.contains(tag.getKey())) {
                        line = line.replace(tag.getKey(), tag.getValue());
                    }
                }
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Could not create project file :");
            System.err.println(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close write buffer: ");
                System.err.println(e);
            }
        }
    }

    public static void insertTemplateWithReplaceBeforeString(
            final File source, final String beforeString, final File template, final Map<String, String> tags
    ) {
        BufferedWriter writer = null;
        InputStream templateStream;
        try {
            templateStream = new FileInputStream(
                    TemplatePathBuilder.buildTemplatePath(template.getName())
            );
        } catch (IOException e) {
            System.out.println("Could not open template file");

            return;
        }
        StringBuilder insertingSection = new StringBuilder("") ;
        try (Scanner scanner = new Scanner(templateStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    if (line.contains(tag.getKey())) {
                        line = line.replace(tag.getKey(), tag.getValue());
                    }
                }
                insertingSection
                        .append(line)
                        .append("\n");
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Could not replace tags in the given template :");
            System.err.println(e);
        }
        StringBuilder updatingFile = new StringBuilder("");
        try (Scanner scanner = new Scanner(source)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(beforeString)) {
                    updatingFile
                            .append(insertingSection);
                }
                updatingFile
                        .append(line)
                        .append("\n");
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Could not insert template to the given file :");
            System.err.println(e);
        }
        try {
            writer = new BufferedWriter(new FileWriter(source));
            writer.write(updatingFile.toString());
        } catch (IOException e) {
            System.out.println("Could not update file :");
            System.err.println(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close write buffer: ");
                System.err.println(e);
            }
        }
    }
}
