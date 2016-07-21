package info.smart_tools.smartactors.actors.close_async_operation.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for CloseAsyncOperationActor parameters
 */
public interface ActorParams {
    /**
     *
     * @return the name of collection where async op's are stored
     * @throws ReadValueException Throw when can't correct read value
     */
    String getCollectionName() throws ReadValueException;
}
