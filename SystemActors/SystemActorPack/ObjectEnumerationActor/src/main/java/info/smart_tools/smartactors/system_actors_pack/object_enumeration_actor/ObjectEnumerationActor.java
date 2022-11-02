package info.smart_tools.smartactors.system_actors_pack.object_enumeration_actor;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.system_actors_pack.object_enumeration_actor.wrapper.EnumerationResult;

/**
 * Actor that enumerates messaging system objects (receivers and chains).
 */
public class ObjectEnumerationActor {
    /**
     * Enumerates all chains stored in global chain storage.
     *
     * @param result    the message wrapper to store list of chain identifiers in
     * @throws ResolutionException if cannot resolve global chain storage
     * @throws ChangeValueException if cannot write result to message wrapper
     */
    public void enumerateChains(final EnumerationResult result)
            throws ResolutionException, ChangeValueException {
        IChainStorage storage = IOC.resolve(Keys.getKeyByName(IChainStorage.class.getCanonicalName()));
        result.setItems(storage.enumerate());
    }

    /**
     * Enumerates all message receivers registered in global router.
     *
     * @param result    the message wrapper to store list of receiver identifiers in
     * @throws ResolutionException if cannot resolve global router
     * @throws ChangeValueException if cannot write result to message wrapper
     */
    public void enumerateReceivers(final EnumerationResult result)
            throws ResolutionException, ChangeValueException {
        IRouter router = IOC.resolve(Keys.getKeyByName(IRouter.class.getCanonicalName()));
        result.setItems(router.enumerate());
    }
}
