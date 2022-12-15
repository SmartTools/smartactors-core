package info.smart_tools.smartactors.endpoint.interfaces.iendpoint_external_configuration_reader.exception;

/**
 * Default exception for IEndpointExternalConfigurationReader
 */
public class EndpointExternalConfigurationReaderException extends Exception {
    /**
     * Constructor by error message
     * @param msg error message
     */
    public EndpointExternalConfigurationReaderException(String msg) {
        super(msg);
    }

    /**
     * Constructor by error message and cause
     * @param msg error message
     * @param throwable cause
     */
    public EndpointExternalConfigurationReaderException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    /**
     * Constructor by cause
     * @param throwable cause
     */
    public EndpointExternalConfigurationReaderException(Throwable throwable) {
        super(throwable);
    }
}
