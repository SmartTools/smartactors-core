package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ReadJsonException;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.logger.Logger;
import info.smart_tools.smartactors.launcher.model.FeatureConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class FeatureReader implements IFeatureReader {

    private final ILogger log;

    private final IObjectMapper objectMapper;

    public FeatureReader(
            final IObjectMapper objectMapper
    ) {
        this.log = new Logger();
        this.objectMapper = objectMapper;
    }

    @Override
    public List<IFeature> readFeatures(
            final List<IPath> paths
    ) throws FeatureReaderException {
        List<IFeature> features = new ArrayList<>();

        for (IPath file : paths) {
            String pathToJar = file.getPath();
            String jarFileName = null;

            try (JarFile jarFile = new JarFile(pathToJar)) {
                jarFileName = jarFile.getName();
                Enumeration<JarEntry> iterator = jarFile.entries();

                while (iterator.hasMoreElements()) {
                    JarEntry je = iterator.nextElement();
                    String CONFIG_FILENAME = "config.json";
                    if (je.isDirectory() || !je.getName().equals(CONFIG_FILENAME)) {
                        continue;
                    }

                    try (
                            InputStream inputStream = jarFile.getInputStream(je);
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
                    ) {
                        String configRaw = bufferedReader
                                .lines()
                                .collect(Collectors.joining("\n"));

                        FeatureConfig featureConfig = objectMapper.readJson(configRaw, FeatureConfig.class);
                        log.debug("Loaded config.json for feature {0}", featureConfig.getFeatureName());

                        Feature feature = new Feature(
                                UUID.randomUUID(),
                                pathToJar,
                                featureConfig.getFeatureName(),
                                featureConfig.getAfterFeatures(),
                                featureConfig.getPlugins()
                        );
                        features.add(feature);
                    }
                }
            } catch (ReadJsonException e) {
                log.error("Failed to load config.json for {0}", jarFileName);
            } catch (Throwable e) {
                throw new FeatureReaderException("Plugin loading failed: " + pathToJar, e);
            }
        }

        return features;
    }
}
