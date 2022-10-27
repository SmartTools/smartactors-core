package info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.wrappers;


import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for {@link info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.HttpRequestSenderActor}
 */
public interface HttpRequestSenderActorWrapper {
    IObject getRequest() throws ReadValueException;
}
