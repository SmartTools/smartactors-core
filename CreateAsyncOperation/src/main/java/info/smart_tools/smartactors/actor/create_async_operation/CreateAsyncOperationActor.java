package info.smart_tools.smartactors.actor.create_async_operation;

import info.smart_tools.smartactors.actor.create_async_operation.exception.CreateAsyncOperationActorException;
import info.smart_tools.smartactors.actor.create_async_operation.wrapper.AuthOperationData;
import info.smart_tools.smartactors.actor.create_async_operation.wrapper.CreateAsyncOperationMessage;
import info.smart_tools.smartactors.core.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.core.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.UUID;

/**
 * Actor for creation asynchronous operation
 */
public class CreateAsyncOperationActor {

    /**
     * Constructor needed for registry actor
     * @param params iobject
     */
    public CreateAsyncOperationActor(final IObject params) {
    }

    /**
     * Generates token and creates asynchronous operation.
     * Sets token to session and response.
     * @param message {
     *                "sessionId": "session identifier for save as async data",
     *                "expiredTime": "TTL for async operation"
     * }
     * @throws CreateAsyncOperationActorException for creation error
     */
    public void create(final CreateAsyncOperationMessage message) throws CreateAsyncOperationActorException {

        try {
            //TODO:: move generate to util class and add server number
            String token = String.valueOf(UUID.randomUUID());
            IAsyncOperationCollection collection = IOC.resolve(Keys.getOrAdd(IAsyncOperationCollection.class.toString()));
            String expiredTime = message.getExpiredTime();
            AuthOperationData authOperationData = IOC.resolve(Keys.getOrAdd(AuthOperationData.class.toString()));
            authOperationData.setSessionId(message.getSessionId());
//            collection.createAsyncOperation(((IObjectWrapper) authOperationData).getIObjects()[0], token, expiredTime);
            collection.createAsyncOperation(IOC.resolve(Keys.getOrAdd(IObject.class.toString()), authOperationData), token, expiredTime);

            //NOTE: this setter should set token to session and to response!
            message.setAsyncOperationToken(token);
        } catch (ReadValueException | ResolutionException | ChangeValueException | CreateAsyncOperationException e) {
            throw new CreateAsyncOperationActorException("Can't create async operation.", e);
        }
    }
}
