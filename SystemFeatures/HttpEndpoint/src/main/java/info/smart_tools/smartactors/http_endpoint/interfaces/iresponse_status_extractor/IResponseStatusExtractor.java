package info.smart_tools.smartactors.http_endpoint.interfaces.iresponse_status_extractor;


import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for extracting http response status from environment
 */
public interface IResponseStatusExtractor {
    /**
     * Method for extracting http response status from environment
     *
     * @param environment Environment of the message processor
     * @return Status code
     */

    Integer extract(IObject environment);
}
