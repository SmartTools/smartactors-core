package info.smart_tools.smartactors.async_operations.create_async_operation;

import info.smart_tools.smartactors.async_operations.create_async_operation.exception.CreateAsyncOperationActorException;
import info.smart_tools.smartactors.async_operations.create_async_operation.wrapper.CreateAsyncOperationMessage;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.async_operation_collection.IAsyncOperationCollection;
import info.smart_tools.smartactors.database.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Actor for creation asynchronous operation
 */
public class CreateAsyncOperationActor {

    private IAsyncOperationCollection collection;

    private DateTimeFormatter formatter;

    /**
     * Constructor needed for registry actor
     * @param params iobject
     * @throws CreateAsyncOperationActorException if any error is occurred
     */
    public CreateAsyncOperationActor(final IObject params) throws CreateAsyncOperationActorException {
        try {
            formatter = IOC.resolve(Keys.getKeyByName("datetime_formatter"));
            IField collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            IField databaseOptionsF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "databaseOptions");
            Object connectionOpts =  IOC.resolve(Keys.getKeyByName(databaseOptionsF.in(params)));
            collection = IOC.resolve(
                Keys.getKeyByName(IAsyncOperationCollection.class.getCanonicalName()), connectionOpts, collectionNameField.in(params)
            );
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new CreateAsyncOperationActorException("Can't read collection name from message", e);
        } catch (ResolutionException e) {
            throw new CreateAsyncOperationActorException("Can't get key or resolve dependency", e);
        }
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
            String token = IOC.resolve(Keys.getKeyByName("db.collection.nextid"));
            Integer amountOfHoursToExpireFromNow = message.getExpiredTime();
            String expiredTime = LocalDateTime.now().plusHours(amountOfHoursToExpireFromNow).format(formatter);
            message.setSessionIdInData(message.getSessionId());
            IObject authOperationData = message.getOperationData();
            collection.createAsyncOperation(authOperationData, token, expiredTime);

            message.setAsyncOperationToken(token);

            List<String> availableTokens = message.getOperationTokens();
            if (availableTokens == null) {
                message.setOperationTokens(Arrays.asList(token));
            } else {
                availableTokens.add(token);
                message.setOperationTokens(availableTokens);
            }
        } catch (ResolutionException | ReadValueException | ChangeValueException | CreateAsyncOperationException e) {
            throw new CreateAsyncOperationActorException("Can't create async operation.", e);
        }
    }
}
