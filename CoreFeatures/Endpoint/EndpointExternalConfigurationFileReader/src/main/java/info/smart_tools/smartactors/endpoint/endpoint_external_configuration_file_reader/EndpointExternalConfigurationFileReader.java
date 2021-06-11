package info.smart_tools.smartactors.endpoint.endpoint_external_configuration_file_reader;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader.IEndpointExternalConfigurationReader;
import info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader.exception.EndpointExternalConfigurationReaderException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * {@link IEndpointExternalConfigurationReader} implementation for reading external configuration from file
 */
public class EndpointExternalConfigurationFileReader implements IEndpointExternalConfigurationReader {
    private final Properties props;

    /**
     * This constructor loads external configuration from file that lays in configuration.pathToExternalConfiguration
     * @param configuration                                 default endpoint configuration
     * @throws EndpointExternalConfigurationReaderException if something went wrong while loading
     */
    public EndpointExternalConfigurationFileReader(final IObject configuration) throws EndpointExternalConfigurationReaderException {
        try (FileReader propsReader = new FileReader((String) configuration.getValue(
                IOC.resolve(
                    Keys.getKeyByName(IFieldName.class.getCanonicalName()),
                    "pathToExternalConfiguration"
                )
        ))) {
            props = new Properties();
            props.load(propsReader);
        } catch (FileNotFoundException e) {
            throw new EndpointExternalConfigurationReaderException("Unable to find properties file", e);
        } catch (IOException e) {
            throw new EndpointExternalConfigurationReaderException("Failed work with properties file", e);
        } catch (ResolutionException e) {
            throw new EndpointExternalConfigurationReaderException("Failed resolve value from IOC", e);
        } catch (ReadValueException e) {
            throw new EndpointExternalConfigurationReaderException("Failed read value from IObject", e);
        } catch (InvalidArgumentException e) {
            throw new EndpointExternalConfigurationReaderException("Failed work with IObject", e);
        } catch (NullPointerException e) {
            throw new EndpointExternalConfigurationReaderException("Failed work with null", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T read(String propertyName) {
        return (T) props.getProperty(propertyName);
    }

    @Override
    public <T> T readOrDefault(String propertyName, T defaultValue) {
        T value = read(propertyName);
        return value != null ? value : defaultValue;
    }
}
