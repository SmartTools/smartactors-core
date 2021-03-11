package info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader;


import info.smart_tools.smartactors.endpoint.iendpoint_external_configuration_reader.exception.EndpointExternalConfigurationReaderException;

/**
 * Interface for reader of external endpoint configurations
 */
public interface IEndpointExternalConfigurationReader {
    /**
     * This method returns value of configuration with given key
     * @param key                                           name of configuration key
     * @param <T>                                           value
     * @return                                              value of configuration with given key
     * @throws EndpointExternalConfigurationReaderException if something went wrong while reading
     */
    <T> T read(String key) throws EndpointExternalConfigurationReaderException;

    /**
     * This method returns value of configuration with given key if all fine or default value else
     * @param key                                           name of configuration key
     * @param defaultValue                                  default value
     * @param <T>                                           value
     * @return                                              value of configuration with given key if all fine or default value else
     * @throws EndpointExternalConfigurationReaderException if something went wrong while reading
     */
    <T> T readOrDefault(String key, T defaultValue) throws EndpointExternalConfigurationReaderException;
}
