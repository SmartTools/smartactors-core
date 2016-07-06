package info.smart_tools.smartactors.actors.create_user.wrapper;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for message
 */
public interface MessageWrapper {
    /**
     * getter for User
     * @return user
     * @throws ReadValueException
     */
    IObject getUser() throws ReadValueException;
}
