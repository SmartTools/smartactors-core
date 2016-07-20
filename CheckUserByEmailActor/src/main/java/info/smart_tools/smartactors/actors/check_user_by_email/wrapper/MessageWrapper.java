package info.smart_tools.smartactors.actors.check_user_by_email.wrapper;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for message for {@link info.smart_tools.smartactors.actors.check_user_by_email.CheckUserByEmailActor}
 */
public interface MessageWrapper {
    /**
     * Return email from message
     * @return email
     * @throws ReadValueException Throw when can't correct read value
     */
    String getEmail() throws ReadValueException;

    /**
     * Setter for user
     * @param user the user from db
     * @throws ChangeValueException Throw when can't correct change value
     */
    void setUser(IObject user) throws ChangeValueException;
}
