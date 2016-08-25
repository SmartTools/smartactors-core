package info.smart_tools.smartactors.actors.get_async_operation;

import info.smart_tools.smartactors.actors.get_async_operation.exception.GetAsyncOperationActorException;
import info.smart_tools.smartactors.actors.get_async_operation.wrapper.GetAsyncOperationMessage;
import info.smart_tools.smartactors.core.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Actor for read asynchronous operation by token
 */
public class GetAsyncOperationActor {

    private IAsyncOperationCollection collection;

    /**
     * Constructor needed for registry actor
     * @param params iobject
     * @throws GetAsyncOperationActorException if any error is occurred
     */
    public GetAsyncOperationActor(final IObject params) throws GetAsyncOperationActorException {
        try {
            IField collectionNameField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
            collection = IOC.resolve(
                Keys.getOrAdd(IAsyncOperationCollection.class.getCanonicalName()), (String) collectionNameField.in(params)
            );
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new GetAsyncOperationActorException("Can't read collection name from message", e);
        } catch (ResolutionException e) {
            throw new GetAsyncOperationActorException("Can't get key or resolve dependency", e);
        }
    }

    /**
     * Gets operation from collection of asynchronous operations.
     * Sets operation to message.
     * @param message {
     *                "token": "unique identifier of operation"
     * }
     * @throws GetAsyncOperationActorException for read error
     */
    public void getOperation(final GetAsyncOperationMessage message) throws GetAsyncOperationActorException {

        try {
            String token = message.getToken();
            if (token == null || token.equals("")) {
                throw new GetAsyncOperationException("Token of asynchronous operation is null or empty");
            }
            IObject asyncOperation = collection.getAsyncOperation(token);
            if (asyncOperation == null) {
                throw new GetAsyncOperationActorException("Asynchronous operation by token " + token + " is null");
            }
            message.setAsyncOperation(asyncOperation);
        } catch (ChangeValueException | GetAsyncOperationException | ReadValueException e) {
            throw new GetAsyncOperationActorException("Can't get async operation by token.", e);
        }
    }
}
