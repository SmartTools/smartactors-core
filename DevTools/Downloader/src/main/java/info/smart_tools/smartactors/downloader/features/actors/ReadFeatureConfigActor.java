package info.smart_tools.smartactors.downloader.features.actors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.downloader.features.Feature;

import java.nio.file.Path;

public class ReadFeatureConfigActor extends StatelessActor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReadFeatureConfigActor() {
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Executable()
    public Feature readFeatureConfig(final Path config) {
        Feature feature;
        try {
            feature = this.objectMapper.readValue(config.toFile(), Feature.class);
        } catch (Exception e) {
            System.err.println("Failed to get feature data from file " + config.toFile().getName() + " .");
            throw new RuntimeException("Could not read file", e);
        }

        return feature;
    }
}
