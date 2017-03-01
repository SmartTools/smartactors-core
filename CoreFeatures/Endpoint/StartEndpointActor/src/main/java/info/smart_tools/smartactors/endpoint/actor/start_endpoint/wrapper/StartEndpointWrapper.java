package info.smart_tools.smartactors.endpoint.actor.start_endpoint.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper for {@link info.smart_tools.smartactors.endpoint.actor.start_endpoint.StartEndpointActor}
 */
public interface StartEndpointWrapper {
    IObject getEndpointConfiguration() throws ReadValueException;
}
