package info.smart_tools.smartactors.uploader.features.actors;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.uploader.features.Feature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LoadFeatureConfigActor extends StatelessActor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Executable
    public Feature loadFeatureConfig(final List<File> files) {
        Feature feature = null;

        for (File file : files) {
            try {
                JarFile jarFile = new JarFile(file.getAbsolutePath());

                Enumeration<JarEntry> iterator = jarFile.entries();
                while (iterator.hasMoreElements() && feature == null) {
                    JarEntry je = iterator.nextElement();
                    String CONFIG_FILENAME = "config.json";
                    if (je.isDirectory() || !je.getName().equals(CONFIG_FILENAME)) {
                        continue;
                    }

                    try (InputStream inputStream = jarFile.getInputStream(je)) {
                        feature = objectMapper.readValue(inputStream, Feature.class);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to open file " + file.getAbsolutePath(), e);
            }
        }

        return feature;
    }
}
