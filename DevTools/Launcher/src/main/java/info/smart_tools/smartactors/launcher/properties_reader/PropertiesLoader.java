package info.smart_tools.smartactors.launcher.properties_reader;

import info.smart_tools.smartactors.launcher.interfaces.exception.iproperties_reader.PropertiesReaderException;
import info.smart_tools.smartactors.launcher.interfaces.iproperties_reader.IPropertiesLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader implements IPropertiesLoader {

    private final String propertiesPath;

    public PropertiesLoader(
            final String propertiesPath
    ) {
        this.propertiesPath = propertiesPath;
    }

    @Override
    public Map<Object, Object> loadProperties() throws PropertiesReaderException {
        try (Reader reader = new FileReader(propertiesPath)) {
            Properties properties = new Properties();
            properties.load(reader);
            return new HashMap<>(properties);
        } catch (FileNotFoundException e) {
            throw new PropertiesReaderException(MessageFormat.format("Failed to find properties at path \"{0}\"", propertiesPath), e);
        } catch (IOException e) {
            throw new PropertiesReaderException(MessageFormat.format("Failed to read properties at path \"{0}\"", propertiesPath), e);
        }
    }
}
