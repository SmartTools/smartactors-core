package info.smart_tools.smartactors.das.utilities;

import java.io.File;

public final class ParameterResolver {

    private ParameterResolver() {
    }

    public static String getModuleDirectoryName(final String str) {

        return str.substring(str.lastIndexOf('.') + 1);
    }

    public static String getArtifactId(final String str) {

        return str.trim().replaceAll("(([^.I])([A-Z]))", "$2-$3").toLowerCase();
    }

    public static String getGroupId(final String str) {

        return str.trim().replaceAll("(([^.I])([A-Z]))", "$2-$3").toLowerCase();
    }

    public static String getDirectoryStructure(final String str) {

        return str.trim().replaceAll("(([^.I])([A-Z]))", "$2_$3").replaceAll("-", "_").replace(".", File.separator).toLowerCase();
    }

    public static String getPackageName(final String str) {

        return str.trim().replaceAll("(([^.I])([A-Z]))", "$2_$3").replaceAll("-", "_").toLowerCase();
    }

    public static String getClassName(final String str) {

        return str.substring(str.lastIndexOf('.') + 1);
    }
}
