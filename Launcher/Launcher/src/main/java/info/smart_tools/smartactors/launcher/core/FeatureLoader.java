package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IFeatureLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.FeatureLoaderException;
import info.smart_tools.smartactors.launcher.path.Path;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FeatureLoader implements IFeatureLoader {

    @Override
    public List<IPath> loadFeatures(
            final String coreFeaturesPath
    ) throws FeatureLoaderException {
        if (coreFeaturesPath == null || coreFeaturesPath.equals("")) {
            throw new FeatureLoaderException("Path to core features cannot be either null nor empty");
        }

        File coreDir = new File(coreFeaturesPath);
        File[] files = coreDir.listFiles();
        if (files == null) {
            throw new FeatureLoaderException(MessageFormat.format("No feature files were found at {0}", coreFeaturesPath));
        }

        List<IPath> jars = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                jars.addAll(getListOfJars(file));
            } else if (isJar(file)) {
                jars.add(new Path(file));
            }
        }
        return jars;
    }

    private List<IPath> getListOfJars(final File directory) throws FeatureLoaderException {
        if (!directory.isDirectory()) {
            throw new FeatureLoaderException(MessageFormat.format("File ''{0}'' is not a directory.", directory.getAbsolutePath()));
        }

        File[] files = directory.listFiles(this::isJar);
        List<IPath> paths = new LinkedList<>();
        if (files == null) {
            throw new FeatureLoaderException(MessageFormat.format("No feature files were found at {0}", directory.getAbsolutePath()));
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
