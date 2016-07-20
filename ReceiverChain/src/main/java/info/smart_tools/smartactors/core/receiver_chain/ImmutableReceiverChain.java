package info.smart_tools.smartactors.core.receiver_chain;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;

import java.util.Map;

/**
 * Basic implementation of {@link IReceiverChain} -- immutable sequence of receivers.
 */
public class ImmutableReceiverChain implements IReceiverChain {
    private final String name;
    private final IMessageReceiver[] receivers;
    private final IObject[] arguments;
    private final Map<Class<? extends Throwable>, IReceiverChain> exceptionalChains;

    /**
     * The constructor.
     *
     * @param name                        name of the chain
     * @param receivers                   sequence (array) of receivers
     * @param arguments                   array of argument objects for receivers in the chain
     * @param exceptionalChains           mapping from exception class to exceptional chain to use when it occurs
     * @throws InvalidArgumentException if name is {@code null}
     * @throws InvalidArgumentException if receivers is {@code null}
     */
    public ImmutableReceiverChain(final String name, final IMessageReceiver[] receivers, final IObject[] arguments,
                                  final Map<Class<? extends Throwable>, IReceiverChain> exceptionalChains)
            throws InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Chain name should not be null.");
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

        if (null == exceptionalChains) {
            throw new InvalidArgumentException("Exceptional chains list should not be null");
        }

        this.name = name;
        this.receivers = receivers;
        this.arguments = arguments;
        this.exceptionalChains = exceptionalChains;
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
    public String getName() {
        return name;
    }

    @Override
    public IReceiverChain getExceptionalChain(final Throwable exception) {
        Throwable e = exception;

        do {
            for (Map.Entry<Class<? extends Throwable>, IReceiverChain> entry : this.exceptionalChains.entrySet()) {
                if (entry.getKey().isAssignableFrom(e.getClass())) {
                    return entry.getValue();
                }
            }

            Throwable eNext = e.getCause();

            if (eNext == e) {
                break;
            }

            e = eNext;
        } while (null != e);

        return null;
    }
}
