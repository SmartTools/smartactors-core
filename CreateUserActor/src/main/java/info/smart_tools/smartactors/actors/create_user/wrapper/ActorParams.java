package info.smart_tools.smartactors.actors.create_user.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for parameters
 */
public interface ActorParams {
    /**
     * @return name of collection
     * @throws ReadValueException
     */
    String getCollectionName() throws ReadValueException;
}
