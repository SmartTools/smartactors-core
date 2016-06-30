package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Common wrappers interface
 */
public interface IObjectWrapper {
    /**
     * Init wrapper by message, context and response
     * @param iObjects array of {@link IObject}
     */
    void init(IObject ... iObjects);

    /**
     * Getter for array of init iObjects
     * @return the array of init iObjects
     */
    IObject[] getIObjects();
}
