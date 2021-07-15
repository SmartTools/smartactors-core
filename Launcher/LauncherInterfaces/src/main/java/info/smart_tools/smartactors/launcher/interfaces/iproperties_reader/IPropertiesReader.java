package info.smart_tools.smartactors.launcher.interfaces.iproperties_reader;

import info.smart_tools.smartactors.launcher.interfaces.exception.iproperties_reader.PropertiesReaderException;

import java.util.Map;

/**
 * Abstract properties reader
 *
 * Reads Java properties
 */
public interface IPropertiesReader {

    /**
     * Read properties and return them as a {@link Map}
     *
     * @return loaded properties
     * @throws PropertiesReaderException if failed to read properties
     */
    Map<Object, Object> readProperties() throws PropertiesReaderException;
}
