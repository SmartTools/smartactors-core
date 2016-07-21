package info.smart_tools.smartactors.actors.check_user_is_new.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for params for actor {@link info.smart_tools.smartactors.actors.check_user_is_new.CheckUserIsNewActor}
 */
public interface ActorParams {

    /**
     * getter for collection name
     * @return collection name
     * @throws ReadValueException Throw when can't correct read value
     */
    String getCollectionName() throws ReadValueException;

    /**
     * @return the key for target collection
     * @throws ReadValueException Throw when can't correct read value
     */
    String getCollectionKey() throws ReadValueException;
}
