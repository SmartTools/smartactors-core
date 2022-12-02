package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.interfaces.exception.DeletionCheckException;

/**
 * Strategy used by collection receiver to check if a child receiver should be deleted.
 */
public interface IChildDeletionCheckStrategy {
    /**
     * Check if a child receiver should be deleted.
     *
     * @param creationContext    receiver creation context
     * @param messageEnvironment environment of the last processed message
     * @return {@code true} if the child receiver should be deleted
     * @throws DeletionCheckException if any error occurs
     */
    boolean checkDelete(IObject creationContext, IObject messageEnvironment) throws DeletionCheckException;
}
