package info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response;

import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.exception.DeferredResponseException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;

/**
 * Interface for store deferred response.
 */
public interface IDeferredResponse {

    /**
     * Restore deferred response
     * @param wrapper the wrapper
     */
    void restoreResponse(IObjectWrapper wrapper) throws DeferredResponseException;

}
