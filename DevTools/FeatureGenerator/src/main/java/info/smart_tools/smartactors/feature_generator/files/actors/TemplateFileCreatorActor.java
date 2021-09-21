package info.smart_tools.smartactors.feature_generator.files.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.feature_generator.files.FileBuilder;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class TemplateFileCreatorActor extends StatelessActor {

    private static final String TEMPLATE_EXT = "__template";

    @Executable
    public void createFileFromTemplate(final String template, final String path, final Map<String, String> tags) {
        File target = Paths.get(path, template).toFile();
        FileBuilder.createFileFromTemplate(template + TEMPLATE_EXT, target, tags);
    }
}
