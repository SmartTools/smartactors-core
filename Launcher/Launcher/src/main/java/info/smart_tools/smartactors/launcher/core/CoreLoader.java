package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.ICoreLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreLoaderException;
import info.smart_tools.smartactors.launcher.path.Path;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CoreLoader implements ICoreLoader {

    @Override
    public List<IPath> loadCoreFeature(
            final String coreFeaturesPath
    ) throws CoreLoaderException {
        if (coreFeaturesPath == null || coreFeaturesPath.equals("")) {
            throw new CoreLoaderException("Path to core features cannot be either null nor empty");
        }

        File coreDir = new File(coreFeaturesPath);
        List<IPath> jars = new ArrayList<>();
        File[] files = coreDir.listFiles();
        if (files == null) {
            throw new CoreLoaderException(MessageFormat.format("No feature files were found at {0}", coreFeaturesPath));
        }

        for (File file : files) {
            if (file.isDirectory()) {
                jars.addAll(getListOfJars(file));
            } else if (isJar(file)) {
                jars.add(new Path(file));
            }
        }

        return jars;
    }

    private List<IPath> getListOfJars(
            final File directory
    ) throws CoreLoaderException {
        if (!directory.isDirectory()) {
            throw new CoreLoaderException(MessageFormat.format("File ''{0}'' is not a directory.", directory.getAbsolutePath()));
        }

        File[] files = directory.listFiles(this::isJar);
        List<IPath> paths = new LinkedList<>();
        if (files == null) {
            throw new CoreLoaderException(MessageFormat.format("No feature files were found at {0}", directory.getAbsolutePath()));
        }
        for (File file : files) {
            paths.add(new Path(file));
        }

        return paths;
    }

    private boolean isJar(final File file) {
        return file.isFile() && file.getName().endsWith(".jar");
    }
}
