package info.smart_tools.smartactors.message_processing.chain_modifications;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decorator for {@link IReceiverChain} that replaces some steps of decorated chain by another ones.
 */
class ReceiverReplacingChainDecorator implements IReceiverChain {
    private final IReceiverChain original;
    private final IMessageReceiver[] receivers;

    /**
     * The constructor.
     *
     * @param original        the original chain
     * @param replacements    map defining the receivers to replace
     * @throws InvalidArgumentException if the {@code replacements} map contains index higher than the first empty place in original chain
     */
    ReceiverReplacingChainDecorator(final IReceiverChain original, final Map<Integer, IMessageReceiver> replacements)
            throws InvalidArgumentException {
        int lastReplace = 0;

        for (Integer i : replacements.keySet()) {
            if (lastReplace < i) {
                lastReplace = i;
            }
        }

        if (lastReplace != 0 && original.get(lastReplace - 1) == null) {
            throw new InvalidArgumentException("Replacements contain index higher than the first empty place in original chain.");
        }

        this.receivers = new IMessageReceiver[lastReplace + 1];
        this.original = original;

        for (int i = 0; i < this.receivers.length; i++) {
            this.receivers[i] = replacements.getOrDefault(i, original.get(i));
        }
    }

    @Override
    public IMessageReceiver get(final int index) {
        if (index >= 0 && index < receivers.length) {
            return receivers[index];
        }

        return original.get(index);
    }

    @Override
    public IObject getArguments(final int index) {
        return original.getArguments(index);
    }

    @Override
    public Object getId() {
        return original.getId();
    }

    @Override
    public Object getName() {
        return original.getName();
    }

    @Override
    public IScope getScope() {
        return original.getScope();
    }

    @Override
    public IModule getModule() {
        return original.getModule();
    }

    @Override
    public IObject getExceptionalChainNamesAndEnvironments(final Throwable exception) {
        return original.getExceptionalChainNamesAndEnvironments(exception);
    }

    @Override
    public Collection<Object> getExceptionalChainNames() {
        return original.getExceptionalChainNames();
    }

    @Override
    public IObject getChainDescription() {
        return original.getChainDescription();
    }
}

/**
 * A chain modification strategy that creates a chain with some receivers replaced.
 *
 * <p>
 *     Takes 2 arguments: original chain and modification description. The second one should be alike to the following:
 * </p>
 * <pre>
 *     {
 *         "replacements": [
 *              {
 *                  "step": 0,                                      // Step (starting from zero) of original chain where to replace receiver
 *                                                                  // The maximum permitted step index for modification of a chain
 *                                                                  // containing N receivers is N. When replacing receivers 1..N-1 the
 *                                                                  // original receiver will be passed to replacement strategy. When
 *                                                                  // replacing receiver at N'th step null will be passed instead of
 *                                                                  // original receiver and returned receiver will be appended to the chain
 *                  "dependency": "my receiver upgrade strategy",   // Name of dependency to resolve to create a replacing receiver. The
 *                                                                  // dependency will be resolved with 2 arguments - original receiver (or
 *                                                                  // null) and arguments from the following field.
 *                  "args": {...}                                   // Arguments to pass to replacing receiver resolution dependency.
 *              },
 *              ...
 *         ]
 *     }
 * </pre>
 */
public class ReplaceReceiversChainModificationStrategy implements IStrategy {
    private static final int CHAIN_ARGUMENT_INDEX = 0;
    private static final int MODIFICATION_DESCRIPTION_ARGUMENT_INDEX = 1;

    private final IFieldName replacementsFN;
    private final IFieldName stepFN;
    private final IFieldName dependencyFN;
    private final IFieldName argsFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public ReplaceReceiversChainModificationStrategy()
            throws ResolutionException {
        replacementsFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "replacements");
        stepFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "step");
        dependencyFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency");
        argsFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args");
    }

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        try {
            IReceiverChain originalChain = (IReceiverChain) args[CHAIN_ARGUMENT_INDEX];
            IObject modificationDescription = (IObject) args[MODIFICATION_DESCRIPTION_ARGUMENT_INDEX];

            List<IObject> replacements = (List) modificationDescription.getValue(replacementsFN);

            Map<Integer, IMessageReceiver> replacementMap = new HashMap<>();

            for (IObject replacement : replacements) {
                int step = ((Number) replacement.getValue(stepFN)).intValue();
                IMessageReceiver receiver = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), replacement.getValue(dependencyFN)),
                        originalChain.get(step), replacement.getValue(argsFN)
                );

                replacementMap.put(step, receiver);
            }

            return (T) new ReceiverReplacingChainDecorator(originalChain, replacementMap);
        } catch (ClassCastException | ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new StrategyException(e);
        }
    }
}
