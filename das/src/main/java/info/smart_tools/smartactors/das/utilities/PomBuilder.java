package info.smart_tools.smartactors.das.utilities;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class PomBuilder {

    private PomBuilder() {
    }

    public static void addOrUpdateExecutionSectionToDeployPlugin(
            final File sourceTemplate,
            final File target,
            final Map<String, String> tags
    )
            throws Exception {

        InputStream templateStream;
        try {
            templateStream = new FileInputStream(
                    TemplatePathBuilder.buildTemplatePath(sourceTemplate.getName())
            );
        } catch (IOException e) {
            System.out.println("Could not open template file");

            return;
        }
        StringBuilder configSection = new StringBuilder();
        try (Scanner scanner = new Scanner(templateStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    if (line.contains(tag.getKey())) {
                        line = line.replace(tag.getKey(), tag.getValue());
                    }
                }
                configSection
                        .append(line)
                        .append("\n");
            }
        } catch (Exception e) {
            System.out.println("Could not load feature plugin section template.");
            throw new Exception("Could not load feature plugin section template.");
        }

        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        MavenXpp3WriterWithCustomIndentation mavenWriter = new MavenXpp3WriterWithCustomIndentation("    ");
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            org.apache.maven.model.Plugin plugin = model.getBuild().getPluginManagement().getPlugins().stream()
                    .filter(p -> p.getArtifactId().equals("maven-deploy-plugin"))
                    .findFirst()
                    .orElse(null);
            if (null == plugin) {
                System.out.println("Could not find `maven-deploy-plugin` section.");
                throw new Exception("Could not find `maven-deploy-plugin` section.");
            } else {
                PluginExecution execution = plugin.getExecutions().stream()
                        .filter(e -> e.getId().equals(tags.get("${upload.repository.id}")))
                        .findFirst()
                        .orElse(null);
                if (null == execution) {
                    execution = new PluginExecution();
                    plugin.addExecution(execution);
                }
                execution.setInherited("false");
                execution.setId(tags.get("${upload.repository.id}"));
                execution.setPhase("deploy");
                execution.getGoals().clear();
                execution.getGoals().add("deploy-file");
                Xpp3Dom builtConfigSection = Xpp3DomBuilder.build(new ByteArrayInputStream(configSection.toString().getBytes()), "UTF-8");
                execution.setConfiguration(builtConfigSection);
                plugin.getExecutions();
            }
            FileWriter writer = new FileWriter(target);
            mavenWriter.write(writer, model);
        } catch (Throwable e) {
            System.out.println("Could not add repository to the pom file.");
            throw new Exception("Could not add repository to the pom file.");
        }
    }

    public static void updateVersion(final File target, final String newVersion)
            throws Exception {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        MavenXpp3WriterWithCustomIndentation mavenWriter = new MavenXpp3WriterWithCustomIndentation("    ");
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            model.setVersion(newVersion);
            FileWriter writer = new FileWriter(target);
            mavenWriter.write(writer, model);
        } catch (Throwable e) {
            System.out.println("Could not update version in the pom file.");
            throw new Exception("Could not update version in the pom file.", e);
        }
    }

    public static void updateParentVersion(final File target, final String newVersion)
            throws Exception {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        MavenXpp3WriterWithCustomIndentation mavenWriter = new MavenXpp3WriterWithCustomIndentation("    ");
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            model.getParent().setVersion(newVersion);
            FileWriter writer = new FileWriter(target);
            mavenWriter.write(writer, model);
        } catch (Throwable e) {
            System.out.println("Could not update version in the pom file.");
            throw new Exception("Could not update version in the pom file.", e);
        }

    }

    public static void addDependencies(final File target, final List<Dependency> dependencies)
            throws Exception {
        Model model;
        FileReader reader;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        MavenXpp3WriterWithCustomIndentation mavenWriter = new MavenXpp3WriterWithCustomIndentation("    ");
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            replaceVersion(dependencies);
            model.setDependencies(dependencies);
            FileWriter writer = new FileWriter(target);
            mavenWriter.write(writer, model);
        } catch (Throwable e) {
            System.out.println("Could not add dependencies to the pom file.");
            throw new Exception("Could not add dependencies to the pom file.");
        }
    }

    private static void replaceVersion(final List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            if (dependency.getVersion().equals("${core.version}")) {
                dependency.setVersion("0.6.0");
            }
        }
    }
}
