package info.smart_tools.smartactors.message_processing.receiver_chain;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.dumpable_interface.idumpable.IDumpable;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;

import java.util.*;

/**
 * Basic implementation of {@link IReceiverChain} -- immutable sequence of receivers.
 */
public class ImmutableReceiverChain implements IReceiverChain, IDumpable {
    private final Object id;
    private final Object name;
    private final IMessageReceiver[] receivers;
    private final IObject[] arguments;
    private final Map<Class<? extends Throwable>, IObject> exceptionalChainNamesAndEnv;
    private final IObject description;
    private final Set<Object> allExceptionalChains;
    private final IScope scope;
    private final IModule module;

    /**
     * The constructor.
     *
     * @param id                        id of the chain
     * @param chainDescription            the description of chain
     * @param receivers                   sequence (array) of receivers
     * @param arguments                   array of argument objects for receivers in the chain
     * @param exceptionalChainNamesAndEnv           mapping from exception class to exceptional chain to use when it occurs
     * @throws InvalidArgumentException if id is {@code null}
     * @throws InvalidArgumentException if receivers is {@code null}
     * @throws ResolutionException if cannot resolve any dependency
     * @throws ReadValueException if cannot read chains from {@code exceptionalChainNamesAndEnv}
     */
    public ImmutableReceiverChain(final Object id, final IObject chainDescription, final IMessageReceiver[] receivers, final IObject[] arguments,
                                  final Map<Class<? extends Throwable>, IObject> exceptionalChainNamesAndEnv, IScope scope, IModule module)
            throws InvalidArgumentException, ResolutionException, ReadValueException {
        if (null == id) {
            throw new InvalidArgumentException("Chain id should not be null.");
        }

        if (null == chainDescription) {
            throw new InvalidArgumentException("Chain description should not be null.");
        }

        if (null == receivers) {
            throw new InvalidArgumentException("Chain receivers list should not be null.");
        }

        if (null == arguments) {
            throw new InvalidArgumentException("Chain arguments list should not be null.");
        }

        if (receivers.length != arguments.length) {
            throw new InvalidArgumentException("Length of arguments list  does not match length of receivers list.");
        }

        if (null == exceptionalChainNamesAndEnv) {
            throw new InvalidArgumentException("Exceptional chains list should not be null");
        }

        IFieldName mapIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id");
        IFieldName chainNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");

        this.id = id;
        this.name = chainDescription.getValue(mapIdFieldName);
        this.description = chainDescription;
        this.receivers = receivers;
        this.arguments = arguments;
        this.exceptionalChainNamesAndEnv = exceptionalChainNamesAndEnv;
        this.allExceptionalChains = new HashSet<>();
        for (IObject exceptionEnv : exceptionalChainNamesAndEnv.values()) {
            this.allExceptionalChains.add(exceptionEnv.getValue(chainNameFieldName));
        }
        this.scope = scope;
        this.module = module;
    }

    @Override
    public IMessageReceiver get(final int index) {
        if (index < 0 || index >= receivers.length) {
            return null;
        }

        return receivers[index];
    }

    @Override
    public IObject getArguments(final int index) {
        if (index < 0 || index >= arguments.length) {
            return null;
        }

        return arguments[index];
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Object getName() {
        return name;
    }

    @Override
    public IScope getScope() { return scope; }

    @Override
    public IModule getModule() { return module; }

    @Override
    public IObject getExceptionalChainNamesAndEnvironments(final Throwable exception) {
        Throwable e;
        ArrayList<Class> exceptionClasses = new ArrayList<Class>();
        int i;

        for (Map.Entry<Class<? extends Throwable>, IObject> entry : this.exceptionalChainNamesAndEnv.entrySet()) {
            e = exception;
            i = 0;
            while (null != e) {
                if (exceptionClasses.size() == i ) {
                    try {
                        exceptionClasses.add(ModuleManager.getCurrentClassLoader().loadClass(e.getClass().getName()));
                    }catch (ClassNotFoundException cnf) {
                        exceptionClasses.add(e.getClass());
                    }
                }
                if (entry.getKey().isAssignableFrom(exceptionClasses.get(i))) {
                    return entry.getValue();
                }
                Throwable cause = e.getCause();
                if (cause == e) {
                    break;
                }
                e = cause;
                i++;
            }
        }

        return null;
    }

    @Override
    public IObject getChainDescription() {
        return this.description;
    }

    @Override
    public Collection<Object> getExceptionalChainNames() {
        return allExceptionalChains;
    }

    @Override
    public IObject dump(final IObject options) {
        // As the chain is immutable we may just return the description it was created from. And we do.
        return getChainDescription();
    }
}
