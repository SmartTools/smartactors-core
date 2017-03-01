package info.smart_tools.smartactors.endpoint.actor.stop_endpoint.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Created by sevenbits on 12.11.16.
 */
public interface StopEndpointWrapper {
    String getEndpointName() throws ReadValueException;
}
