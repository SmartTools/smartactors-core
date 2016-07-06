package info.smart_tools.smartactors.core.iobject_wrapper;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Common wrappers interface
 */
public interface IObjectWrapper {
    /**
     * Initialize wrapper by instance of {@link IObject}
     * @param environment instance of {@link IObject}
     */
    void init(IObject environment);

    /**
     * Get specified instance of {@link IObject} by given {@link IFieldName}
     * from init environment
     * @return the specified instance of {@link IObject}
     * @throws InvalidArgumentException if fieldName is null
     * or environment doesn't contain IObject with given fieldName
     */
    IObject getEnvironmentIObject(IFieldName fieldName)
            throws InvalidArgumentException;
}
