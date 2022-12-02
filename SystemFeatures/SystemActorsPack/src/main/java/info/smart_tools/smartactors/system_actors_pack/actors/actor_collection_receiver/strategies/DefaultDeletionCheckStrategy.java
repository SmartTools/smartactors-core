package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.IChildDeletionCheckStrategy;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.exception.DeletionCheckException;

/**
 * {@link IChildDeletionCheckStrategy Deletion check strategy} that decides to delete a child receiver if there is a {@code "deleteChild"}
 * flag set in message context.
 */
public class DefaultDeletionCheckStrategy implements IChildDeletionCheckStrategy {

    private final IFieldName contextFN, deleteFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public DefaultDeletionCheckStrategy() throws ResolutionException {
        contextFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "context");
        deleteFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "deleteChild");
    }

    @Override
    public boolean checkDelete(final IObject creationContext, final IObject messageEnvironment)
        throws DeletionCheckException {
        try {
            IObject messageContext = (IObject) messageEnvironment.getValue(contextFN);
            Object deletionFlag = messageContext.getValue(deleteFN);
            messageContext.deleteField(deleteFN);

            return Boolean.TRUE == deletionFlag;
        } catch (ClassCastException | ReadValueException | DeleteValueException | InvalidArgumentException e) {
            throw new DeletionCheckException(e);
        }
    }
}
