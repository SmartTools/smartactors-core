package info.smart_tools.smartactors.core.actors.check_validity_async_operation.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Message for checking
 */
public interface CheckValidityMessage {

    /**
     * The identifier of asynchronous operation which came from the client
     * @return String Id
     * @throws ReadValueException if any errors occurred
     */
    String getAsyncOperationId() throws ReadValueException;

    /**
     * Returns the list with all identifiers of asynchronous operations which are admissible for this session
     * @return List with identifiers
     * @throws ReadValueException if any errors occurred
     */
    List<String> getIdentifiers() throws ReadValueException;
}
