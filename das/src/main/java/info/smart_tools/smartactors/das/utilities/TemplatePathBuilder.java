package info.smart_tools.smartactors.das.utilities;

import java.io.File;

public final class TemplatePathBuilder {
    private static final String RESOURCE_DIR = "resources";

    private TemplatePathBuilder() {
    }

    public static String buildTemplatePath(final String templateName) {
        return TemplatePathBuilder.getExecutionPath() +
                File.separator + RESOURCE_DIR + File.separator + templateName;
    }

    private static String getExecutionPath() {
        String absolutePath = FileBuilder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));

        return absolutePath;
    }
}
