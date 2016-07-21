package info.smart_tools.smartactors.actors.check_user_by_email.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for params for {@link info.smart_tools.smartactors.actors.check_user_by_email.CheckUserByEmailActor}
 */
public interface ActorParams {
    /**
     * Return the collection name for users
     * @return Name of target collection
     * @throws ReadValueException Throw when can't correct read value
     */
    String getCollectionName() throws ReadValueException;

    /**
     * @return the key for target collection
     * @throws ReadValueException Throw when can't correct read value
     */
    String getCollectionKey() throws ReadValueException;
}
