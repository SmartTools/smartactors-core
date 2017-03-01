package info.smart_tools.smartactors.http_endpoint.deferred_response_actor;

import info.smart_tools.smartactors.http_endpoint.deferred_response_actor.exception.DeferredResponseActorException;
import info.smart_tools.smartactors.http_endpoint.deferred_response_actor.wrapper.DeferredResponseWrapper;
import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.IDeferredResponse;
import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.exception.DeferredResponseException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor for deferred response.
 */
public class DeferredResponseActor {

    /**
     * Storage deferred responses
     */
    private final Map<Object, IDeferredResponse> responses;

    /**
     * Constructor for initial storage deferred responses
     */
    public DeferredResponseActor() {
        responses = new HashMap<>();
    }

    /**
     * Deferred response
     * @param wrapper the wrapper
     * @throws DeferredResponseActorException if any error occurred
     */
    public void deferredResponse(final DeferredResponseWrapper wrapper) throws DeferredResponseActorException {
        try {
            responses.put(wrapper.getDeferredResponseID(), IOC.resolve(Keys.getOrAdd(IDeferredResponse.class.getCanonicalName()),wrapper));
        } catch (ReadValueException | ResolutionException e) {
            throw new DeferredResponseActorException("Can't deferred response", e);
        }
    }

    /**
     * Restore response
     * @param wrapper the wrapper
     * @throws DeferredResponseActorException if any error occurred
     */
    public void restoreResponse(final DeferredResponseWrapper wrapper) throws DeferredResponseActorException {
        try {
            IDeferredResponse deferredResponse = responses.remove(wrapper.getDeferredResponseID());
            deferredResponse.restoreResponse((IObjectWrapper) wrapper);
        } catch (ReadValueException | DeferredResponseException e) {
            throw new DeferredResponseActorException("Can't deferred response", e);
        }
    }

}
