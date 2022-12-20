package info.smart_tools.smartactors.builder.config.strategies;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import info.smart_tools.smartactors.builder.config.IConfigStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class YamlConfigStrategy implements IConfigStrategy {

    private final ObjectMapper yamlReader;
    private final ObjectMapper jsonWriter;

    public YamlConfigStrategy() {
        this.yamlReader = new ObjectMapper(new YAMLFactory());
        this.jsonWriter = new ObjectMapper();

        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        this.jsonWriter.enable(SerializationFeature.INDENT_OUTPUT);
        this.jsonWriter.setDefaultPrettyPrinter(printer);
    }

    @Override
    public void apply(Path featurePath) {
        Path yamlConfigPath = featurePath.resolve("config.yml");
        Path jsonConfigPath = featurePath.resolve("config.json");

        try (
            InputStream is = Files.newInputStream(yamlConfigPath);
            OutputStream os = Files.newOutputStream(jsonConfigPath);
        ) {
            Map<String, Object> config = this.yamlReader.readValue(is, Map.class);

            String serializedConfig = this.jsonWriter.writeValueAsString(config);
            os.write(serializedConfig.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to apply yaml config strategy", e);
        }
    }

    @Override
    public void cleanUp(Path featurePath) {
        try {
            Path jsonConfigPath = featurePath.resolve("config.json");
            Files.delete(jsonConfigPath);
        } catch (IOException e) {
            throw new RuntimeException("Unable to clean up after yaml config strategy", e);
        }
    }
}
