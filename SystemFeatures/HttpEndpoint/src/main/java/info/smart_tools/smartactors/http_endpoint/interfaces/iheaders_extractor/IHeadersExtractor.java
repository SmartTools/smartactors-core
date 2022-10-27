package info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor;


import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for extracting http headers from environment
 */
public interface IHeadersExtractor {
    /**
     * Method for setting http headers from environment
     *
     * @param response Response object in which headers will add
     * @param environment Environment of the message processor
     * @throws HeadersSetterException if there are some problems on setting headers
     */
    void set(Object response, IObject environment) throws HeadersSetterException;

}
