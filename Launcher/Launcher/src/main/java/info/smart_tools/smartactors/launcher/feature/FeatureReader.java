package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ReadJsonException;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
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

    private final IObjectMapper objectMapper;

    public FeatureReader(
            final IObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<IFeature> readFeatures(
            final List<IPath> jars
    ) throws FeatureReaderException {
        List<IFeature> features = new ArrayList<>();

        for (IPath file : jars) {
            String pathToJar = file.getPath();
            String jarFileName = null;

            try (JarFile jarFile = new JarFile(pathToJar)) {
                jarFileName = jarFile.getName();
                Enumeration<JarEntry> iterator = jarFile.entries();

                while (iterator.hasMoreElements()) {
                    JarEntry je = iterator.nextElement();
                    if (je.isDirectory() || !je.getName().equals("config.json")) {
                        continue;
                    }

                    InputStream jarInputStream = jarFile.getInputStream(je);
                    // TODO: close stream?
                    String configRaw = new BufferedReader(new InputStreamReader(jarInputStream))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    FeatureConfig featureConfig = objectMapper.readJson(configRaw, FeatureConfig.class);
//                    System.out.println("[DEBUG] Loaded config.json for feature \"" + featureConfig.getFeatureName() + "\"");

                    Feature feature = new Feature(
                            UUID.randomUUID(),
                            pathToJar,
                            featureConfig.getFeatureName(),
                            featureConfig.getAfterFeatures()
                    );
                    features.add(feature);
                }
            } catch (ReadJsonException e) {
                System.out.println("[ERROR] Failed to load config.json for " + jarFileName);
            } catch (Throwable e) {
                throw new FeatureReaderException("Plugin loading failed: " + pathToJar, e);
            }
        }

        return features;
    }
}
