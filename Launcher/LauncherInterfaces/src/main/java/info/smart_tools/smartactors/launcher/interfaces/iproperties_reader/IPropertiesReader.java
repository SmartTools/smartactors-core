package info.smart_tools.smartactors.launcher.interfaces.iproperties_reader;

import info.smart_tools.smartactors.launcher.interfaces.exception.iproperties_reader.PropertiesReaderException;

import java.util.Map;

public interface IPropertiesReader {

    Map<Object, Object> readProperties() throws PropertiesReaderException;
}
