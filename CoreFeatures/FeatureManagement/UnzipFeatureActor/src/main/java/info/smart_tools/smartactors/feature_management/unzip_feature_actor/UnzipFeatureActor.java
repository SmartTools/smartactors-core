package info.smart_tools.smartactors.feature_management.unzip_feature_actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.exception.UnzipFeatureException;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.wrapper.UnzipFeatureWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Actor that unpack feature
 */
public class UnzipFeatureActor {

    private final IFieldName featureNameFN;
    private final IFieldName dependenciesFieldName;

    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final String IOBJECT_FACTORY_STRATEGY_NAME = "info.smart_tools.smartactors.iobject.iobject.IObject";
    private static final String FIELD_NAME_FACTORY_STARTEGY_NAME =
            "info.smart_tools.smartactors.iobject.ifield_name.IFieldName";
    private static final String ARCHIVE_POSTFIX = "archive";
    private static final String FEATURE_NAME_DELIMITER = ":";
    private static final String END_OF_INPUT_DELIMITER = "\\Z";
    private static final String NAME_OF_CHECK_FILE = ".checkfile";

    private final Map<String, IFunctionTwoArgs<File, IFeature, File>> unzipFunctions;

    /**
     * Default constructor
     * @throws ResolutionException if any errors occurred on resolution of IOC dependencies
     */
    public UnzipFeatureActor()
            throws ResolutionException {
        this.dependenciesFieldName = IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "afterFeatures");
        this.featureNameFN =         IOC.resolve(Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "featureName");

        //TODO: need refactoring. This actions would be took out to the plugin.
        this.unzipFunctions = new HashMap<String, IFunctionTwoArgs<File, IFeature, File>>() {{
            put("zip", (file, feature) -> {
                try {
                    return unzip0(file, feature);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            put("jar", (file, feature) -> {
                try {
                    return copyJar(file, feature);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }};
    }

    /**
     * Unpacks feature
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws UnzipFeatureException if any errors occurred on unpacking feature
     */
    public void unzip(final UnzipFeatureWrapper wrapper)
            throws UnzipFeatureException {
        IFeature feature;
        try {
            feature = wrapper.getFeature();
        } catch (ReadValueException e) {
            throw new UnzipFeatureException("Feature should not be null.");
        }
        try {
            if (null == feature.getDependencies()) {
                System.out.println("[INFO] Start unzipping/copying feature '" + feature.getDisplayName() + "'.");
                File f = new File(feature.getLocation().toString());
                IFunctionTwoArgs<File, IFeature, File> function = this.unzipFunctions.get(getExtension(f));
                if (null != function) {
                    File configFile = function.execute(f, feature);
                    updateFeature(configFile, feature);
                    System.out.println("[OK] -------------- Feature '" + feature.getDisplayName() + "' unzipped/copied successfully.");
                }
            }
        } catch (Throwable e) {
            feature.setFailed(true);
            System.out.println("[FAILED] ---------- Feature '" + feature.getDisplayName() + "' unzipping/copying failed:");
            System.out.println(e);
        }
    }

    private File unzip0(final File f, final IFeature feature)
            throws Exception {
        String destination = f.getPath();
        if (f.getPath().lastIndexOf(File.separator) >= 0) {
            destination = f.getPath().substring(0, f.getPath().lastIndexOf(File.separator));
        }
        try {
            if (checkFeatureWasUpdated(f, feature)) {
                ZipFile zipFile = new ZipFile(f);
                zipFile.extractAll(destination);
                createCheckFile(
                        getFeatureDirectory(f).toFile()
                );
            }

            return Paths.get(this.getFeatureDirectory(f).toString(), CONFIG_FILE_NAME).toFile();
        } catch (ZipException e) {
            throw new Exception("Unsupported feature format: broken archive.", e);
        } catch (NoSuchElementException e) {
            throw new Exception("Unsupported feature format: config.json not found or it's broken.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private File copyJar(final File f, final IFeature feature)
            throws Exception {
        try {
            java.nio.file.Path dir = Files.createDirectories(
                    getFeatureDirectory(f)
            );
            if (checkFeatureWasUpdated(f, feature)) {
                Files.copy(
                        Paths.get(f.getPath()),
                        Paths.get(dir.toString(), f.getName()),
                        StandardCopyOption.REPLACE_EXISTING
                );
                ZipFile zipFile = new ZipFile(f);
                List<FileHeader> headers = zipFile.getFileHeaders();
                FileHeader configFileHeader = headers
                        .stream()
                        .filter(fh -> {
                            String[] names = fh.getFileName().split(File.pathSeparator);
                            return names[names.length - 1].equals(CONFIG_FILE_NAME);
                        })
                        .findFirst()
                        .get();
                if (null != configFileHeader) {
                    zipFile.extractFile(configFileHeader, dir.toString());
                }
                createCheckFile(dir.toFile());
            }

            return new File(Paths.get(dir.toString(), CONFIG_FILE_NAME).toString());
        } catch (ZipException e) {
            throw new Exception("Unsupported feature format: broken archive.", e);
        } catch (NoSuchElementException e) {
            throw new Exception("Unsupported feature format: config.json not found or it's broken.", e);
        }
    }

    private boolean checkFeatureWasUpdated(final File zippedFeature, final IFeature feature) {
        try {
            File destinationDir = this.getFeatureDirectory(zippedFeature).toFile();
            if (destinationDir.exists() && destinationDir.isDirectory()) {
                File checkFile = Paths.get(destinationDir.getPath(), NAME_OF_CHECK_FILE).toFile();
                if (checkFile.exists() && zippedFeature.lastModified() < checkFile.lastModified()) {
                    System.out.println("[INFO] Unzipping/copying feature '" + feature.getDisplayName() + "' already done.");
                    return false;
                }
            }
        } catch (Exception e) {
            return true;
        }

        return true;
    }

    private java.nio.file.Path getFeatureDirectory(final File f) {
        String location = f.getPath();
        if (f.getPath().lastIndexOf(File.separator) >= 0) {
            location = f.getPath().substring(0, f.getPath().lastIndexOf(File.separator));
        }
        String featureName = f.getName().substring(0, f.getName().lastIndexOf(EXTENSION_SEPARATOR));
        if (featureName.endsWith("-" + ARCHIVE_POSTFIX)) {
            featureName = featureName.substring(0, featureName.length() - 8);
        }
        return Paths.get(
                location, featureName
        );
    }

    @SuppressWarnings("unchecked")
    private void updateFeature(final File f, final IFeature feature)
            throws Exception {
        String content = new Scanner(f).useDelimiter(END_OF_INPUT_DELIMITER).next();
        IObject config = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME), content);
        List<String> dependencies = (List<String>) config.getValue(this.dependenciesFieldName);
        String fullFeatureName = (String) config.getValue(this.featureNameFN);
        String[] featureNames = parseFullName(fullFeatureName);
        Set<String> dependenciesSet = new HashSet<>(dependencies);
        feature.setDependencies(dependenciesSet);
        feature.setLocation(new Path(f.getParent()));
        feature.setGroupId(featureNames[0]);
        feature.setName(featureNames[1]);
        if (!featureNames[2].equals("")) {
            feature.setVersion(featureNames[2]);
        }
    }

    private String getExtension(final File f) {
        return f.getName().substring(f.getName().lastIndexOf(EXTENSION_SEPARATOR) + 1);
    }

    private void createCheckFile(final File directory)
            throws Exception {
        File f = Paths.get(directory.getPath(), NAME_OF_CHECK_FILE).toFile();
        if (f.exists()) {
            f.setLastModified(System.currentTimeMillis());
        } else {
            Files.createFile(f.toPath());
        }
    }

    private String[] parseFullName(final String fullName)
            throws UnzipFeatureException {
        String[] dependencyNames = fullName.split(FEATURE_NAME_DELIMITER);
        if (dependencyNames.length < 2) {
            throw new UnzipFeatureException("Wrong feature name or dependency format '" + fullName + "'.");
        }
        String[] result = {
                dependencyNames[0],
                dependencyNames[1],
                dependencyNames.length > 2 ? dependencyNames[2] : ""
        };
        return result;
    }
}
