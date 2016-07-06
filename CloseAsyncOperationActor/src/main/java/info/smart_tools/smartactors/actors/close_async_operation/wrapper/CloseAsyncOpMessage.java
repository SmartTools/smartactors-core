package info.smart_tools.smartactors.actors.close_async_operation.wrapper;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Wrapper for message
 */
public interface CloseAsyncOpMessage {
    /**
     * Getter for token
     * @return token of async operation
     * @throws ReadValueException
     */
    String getToken() throws ReadValueException;

    /**
     * Return async operation
     * @return IObject async operation
     * @throws ReadValueException
     */
    IObject getOperation() throws ReadValueException;

    /**
     * Getter
     * @return list of tokens of async op's
     * @throws ReadValueException
     */
    List<String> getOperationTokens() throws ReadValueException;
}
