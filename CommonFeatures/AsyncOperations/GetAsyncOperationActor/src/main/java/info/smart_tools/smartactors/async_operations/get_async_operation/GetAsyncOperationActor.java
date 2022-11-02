package info.smart_tools.smartactors.async_operations.get_async_operation;

import info.smart_tools.smartactors.async_operations.get_async_operation.exception.GetAsyncOperationActorException;
import info.smart_tools.smartactors.async_operations.get_async_operation.wrapper.GetAsyncOperationMessage;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.database.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

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
            IField collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            IField databaseOptionsF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "databaseOptions");
            Object connectionOpts =  IOC.resolve(Keys.getKeyByName(databaseOptionsF.in(params)));
            collection = IOC.resolve(
                Keys.getKeyByName(IAsyncOperationCollection.class.getCanonicalName()), connectionOpts, collectionNameField.in(params)
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
