package info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject;

import info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject.exception.AddRequestParametersToIObjectException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for classes, that extracting parameters from request to IObject
 */
public interface IAddRequestParametersToIObject {

    void extract(IObject message, Object request) throws AddRequestParametersToIObjectException;
}
