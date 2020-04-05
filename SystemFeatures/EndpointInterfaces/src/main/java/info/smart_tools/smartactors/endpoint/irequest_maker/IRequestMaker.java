package info.smart_tools.smartactors.endpoint.irequest_maker;

import info.smart_tools.smartactors.endpoint.irequest_maker.exception.RequestMakerException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Request maker for client
 */
public interface IRequestMaker<T> {
    T make(IObject request) throws RequestMakerException;
}
