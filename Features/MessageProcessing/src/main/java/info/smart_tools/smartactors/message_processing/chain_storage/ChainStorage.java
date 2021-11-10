package info.smart_tools.smartactors.message_processing.chain_storage;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_storage.interfaces.IChainState;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainModificationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage}.
 */
public class ChainStorage implements IChainStorage {
    private final Map<Object, IChainState> chainStates;
    private final IRouter router;
    private final Object modificationLock = new Object();

    /**
     * The constructor.
     *
     * @param chainStates  {@link Map} to store chains in
     * @param router       {@link IRouter} to use to resolve receivers
     * @throws InvalidArgumentException if chainsMap is {@code null}
     * @throws InvalidArgumentException if router is {@code null}
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public ChainStorage(final Map<Object, IChainState> chainStates, final IRouter router)
            throws InvalidArgumentException, ResolutionException {
        if (null == chainStates) {
            throw new InvalidArgumentException("Chains map should not be null.");
        }

        if (null == router) {
            throw new InvalidArgumentException("Router should not be null.");
        }

        this.chainStates = chainStates;
        this.router = router;
    }

    private IChainState resolveState(final Object chainId) throws ChainNotFoundException {
        IChainState state = chainStates.get(chainId);

з        if (null == state) {
            throw new ChainNotFoundException(chainId);
        }

        return state;
    }

    @Override
    public void register(final Object chainId, final IObject description)
            throws ChainCreationException {
        try {
            IReceiverChain newChain = IOC.resolve(
                    Keys.getKeyByName(IReceiverChain.class.getCanonicalName()),
                    chainId,
                    description,
                    router,
                    ScopeProvider.getCurrentScope(),
                    ModuleManager.getCurrentModule()
            );

            IChainState oldState;

            IChainState state = IOC.resolve(Keys.getKeyByName(IChainState.class.getCanonicalName()), newChain);

            synchronized (modificationLock) {
                oldState = chainStates.put(chainId, state);
            }

            if (null != oldState) {
                System.out.println(MessageFormat.format("[WARNING] Replacing chain ({0}) registered as ''{1}'' by {2}",
                        oldState.getCurrent().toString(), chainId.toString(), newChain.toString()));
            }
        } catch (ResolutionException | ScopeProviderException e) {
            throw new ChainCreationException(MessageFormat.format("Could not create a chain ''{0}''", chainId.toString()), e);
        }
    }

    @Override
    public void unregister(final Object chainId) {
        IChainState oldState;

        synchronized (modificationLock) {
            oldState = chainStates.remove(chainId);
        }

        if (null == oldState) {
            System.out.println(MessageFormat.format("[WARNING] ''{1}'' has no chains, nothing to delete",
                    chainId.toString()));
        }
    }

    @Override
    public IReceiverChain resolve(final Object chainId)
            throws ChainNotFoundException {
        return resolveState(chainId).getCurrent();
    }

    @Override
    public List<Object> enumerate() {
        return new ArrayList<>(chainStates.keySet());
    }

    @Override
    public Object update(final Object chainId, final IObject modification)
            throws ChainNotFoundException, ChainModificationException {
        synchronized (modificationLock) {
            try {
                return resolveState(chainId).update(modification);
            } catch (ChainModificationException e) {
                throw new ChainModificationException(
                        MessageFormat.format("Error occurred modifying a receiver chain ''{0}''.", chainId.toString()),
                        e);
            }
        }
    }

    @Override
    public void rollback(final Object chainId, final Object modId) throws ChainNotFoundException, ChainModificationException {
        resolveState(chainId).rollback(modId);
    }
}
