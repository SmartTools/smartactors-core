package info.smart_tools.smartactors.actors.check_user_is_new;

import info.smart_tools.smartactors.actors.check_user_is_new.wrapper.ActorParams;
import info.smart_tools.smartactors.actors.check_user_is_new.wrapper.MessageWrapper;
import info.smart_tools.smartactors.core.cached_collection.ICachedCollection;
import info.smart_tools.smartactors.core.cached_collection.exception.GetCacheItemException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import java.util.List;

/**
 * Actor check that this email was not registered before
 */
public class CheckUserIsNewActor {
    private ICachedCollection collection;

    /**
     * Constructor
     * @param params the actor params
     * @throws InvalidArgumentException Throw when can't read some value from message or resolving key or dependency is throw exception
     */
    public CheckUserIsNewActor(final ActorParams params) throws InvalidArgumentException {
        try {
            collection = IOC.resolve(
                    Keys.getOrAdd(ICachedCollection.class.getCanonicalName()),
                    params.getCollectionName(),
                    params.getCollectionKey());
        } catch (ReadValueException e) {
            throw new InvalidArgumentException("Can't read some of message values", e);
        } catch (ResolutionException e) {
            throw new InvalidArgumentException("Can't get key or resolve dependency", e);
        }
    }

    /**
     * Check that this email was not registered before
     * @param message the message, contain email
     * @throws Exception Throw always
     */
    public void check(final MessageWrapper message) throws Exception {
        try {
            List<IObject> users = collection.getItems(message.getEmail());
            if (!users.isEmpty()) {
                throw new TaskExecutionException("User with this email already exists");
            }
        } catch (ReadValueException | GetCacheItemException e) {
            throw new TaskExecutionException("Failed to get email from message", e);
        }
    }
}
