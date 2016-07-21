package info.smart_tools.smartactors.actors.check_user_is_new.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for message for {@link info.smart_tools.smartactors.actors.check_user_is_new.CheckUserIsNewActor} check method
 */
public interface MessageWrapper {

    /**
     * getter for email
     * @return email
     * @throws ReadValueException Throw when can't correct read value
     */
    String getEmail() throws ReadValueException;
}
