package info.smart_tools.smartactors.endpoint.actor.client.wrapper;

import info.smart_tools.smartactors.endpoint.actor.client.ClientActor;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Wrapper for {@link ClientActor}
 */
public interface ClientActorMessage {
    IObject getRequest();

    IObject getRequestSettings();

    void setResponse(IObject response);

    Object getSendingChain();
}
