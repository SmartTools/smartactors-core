package info.smart_tools.smartactors.actors.close_async_operation;

import info.smart_tools.smartactors.actors.close_async_operation.wrapper.ActorParams;
import info.smart_tools.smartactors.actors.close_async_operation.wrapper.CloseAsyncOpMessage;
import info.smart_tools.smartactors.core.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.core.async_operation_collection.exception.CompleteAsyncOperationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Actor that close async operation
 */
public class CloseAsyncOperationActor {
    private IAsyncOperationCollection collection;

    /**
     * Constructor
     * @param params the params for constructor
     * @throws InvalidArgumentException Throw when can't read some value from message or resolving key or dependency is throw exception
     */
    CloseAsyncOperationActor(final ActorParams params) throws InvalidArgumentException {
        try {
            collection = IOC.resolve(Keys.getOrAdd(IAsyncOperationCollection.class.toString()), params.getCollectionName());
        } catch (ReadValueException e) {
            throw new InvalidArgumentException("Can't read collection name from message", e);
        } catch (ResolutionException e) {
            throw new InvalidArgumentException("Can't get key or resolve dependency", e);
        }
    }

    /**
     * Remove token from session and mark operation as comlete
     * @param message the message
     * @throws TaskExecutionException
     */
    void completeAsyncOp(final CloseAsyncOpMessage message) throws InvalidArgumentException {
        try {
            message.getOperationTokens().remove(message.getToken());
            collection.complete(message.getOperation());
        } catch (ReadValueException e) {
            throw new InvalidArgumentException("Can't read some of values in message", e);
        } catch (CompleteAsyncOperationException e) {
            throw new InvalidArgumentException("Can't close async operation with this parameters: " + message, e);
        }
    }
}
