package info.smart_tools.smartactors.http_endpoint.deferred_response_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for deferred response in {@link info.smart_tools.smartactors.http_endpoint.deferred_response_actor.DeferredResponseActor}.
 */
public interface DeferredResponseWrapper {

    /**
     * Return ID for deferred response
     * @return the ID
     */
    Object getDeferredResponseID() throws ReadValueException;

}
