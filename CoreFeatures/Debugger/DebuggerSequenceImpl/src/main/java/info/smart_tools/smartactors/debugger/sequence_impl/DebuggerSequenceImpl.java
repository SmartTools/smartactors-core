package info.smart_tools.smartactors.debugger.sequence_impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.dumpable_interface.idumpable.IDumpable;
import info.smart_tools.smartactors.dumpable_interface.idumpable.exceptions.DumpException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NoExceptionHandleChainException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;

/**
 * Implementation of {@link IDebuggerSequence}.
 */
public class DebuggerSequenceImpl implements IDebuggerSequence, IDumpable {
    private final IMessageProcessingSequence wrapped;

    private Throwable exception;
    private IObject exceptionContext;
    private boolean isInDebugger;
    private boolean isCompleted;

    private static final String DEBUGGER_INTERRUPT_TARGET =
                   ("{" +
                    "   'handler':'interrupt'," +
                    "   'wrapper': {" +
                    "       'in_getSessionId':'context/sessionId'," +
                    "       'in_getProcessor':'processor'" +
                    "   }" +
                    "}").replace('\'', '"');

    private final IObject debuggerArguments;
    private final IMessageReceiver debuggerReceiver;

    /**
     * The constructor.
     *
     * @param sequence           the underlying sequence to use
     * @param debuggerAddress    address of the debugger actor
     * @throws InvalidArgumentException if {@code sequence} is {@code null}
     * @throws InvalidArgumentException if {@code debuggerAddress} is {@code null} or is not valid address
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public DebuggerSequenceImpl(final IMessageProcessingSequence sequence, final Object debuggerAddress)
            throws InvalidArgumentException, ResolutionException {
        if (null == sequence) {
            throw new InvalidArgumentException("Sequence should not be null.");
        }

        if (null == debuggerAddress) {
            throw new InvalidArgumentException("Debugger address should not be null.");
        }

        this.wrapped = sequence;

        debuggerArguments = IOC.resolve(Keys.getKeyByName("configuration object"), DEBUGGER_INTERRUPT_TARGET);

        IRouter router = IOC.resolve(Keys.getKeyByName(IRouter.class.getCanonicalName()));

        try {
            debuggerReceiver = router.route(debuggerAddress);
        } catch (RouteNotFoundException e) {
            throw new InvalidArgumentException("Invalid debugger address.", e);
        }

        isInDebugger = true;
        isCompleted = false;
    }

    @Override
    public boolean isExceptionOccurred() {
        return exception != null;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public void stop() {
        isCompleted = true;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean processException() {
        if (null != exception) {
            try {
                wrapped.catchException(exception, exceptionContext);
                exception = null;
                return true;
            } catch (NoExceptionHandleChainException | NestedChainStackOverflowException | ChangeValueException |
                    InvalidArgumentException | ReadValueException | ChainNotFoundException | ResolutionException |
                    ScopeProviderException e) {
                e.addSuppressed(exception);
                exception = e;
            }
        }

        return false;
    }

    @Override
    public IMessageProcessingSequence getRealSequence() {
        return wrapped;
    }

    @Override
    public void reset() {
        wrapped.reset();
    }

    @Override
    public boolean next() {
        if (isCompleted) {
            return false;
        }

        if (isInDebugger) {
            isInDebugger = false;
        } else {
            isCompleted = !wrapped.next();
            isInDebugger = true;
        }

        return true;
    }

    @Override
    public void goTo(final int level, final int step) throws InvalidArgumentException {
        isCompleted = false;
        wrapped.goTo(level, step);
    }

    @Override
    public void end() {
        wrapped.end();
    }

    @Override
    public int getCurrentLevel() {
        return wrapped.getCurrentLevel();
    }

    @Override
    public int getStepAtLevel(final int level) throws InvalidArgumentException {
        return wrapped.getStepAtLevel(level);
    }

    @Override
    public IMessageReceiver getCurrentReceiver() {
        return isInDebugger ? debuggerReceiver : wrapped.getCurrentReceiver();
    }

    @Override
    public IObject getCurrentReceiverArguments() {
        return isInDebugger ? debuggerArguments : wrapped.getCurrentReceiverArguments();
    }

    @Override
    public void setScopeSwitchingChainName(final Object chainName) {
        wrapped.setScopeSwitchingChainName(chainName);
    }

    @Override
    public void callChain(final Object chainName)
            throws NestedChainStackOverflowException, ResolutionException, ChainNotFoundException, ScopeProviderException {
        wrapped.callChain(chainName);
    }

    @Override
    public void callChainSecurely(final Object chainName, final IMessageProcessor processor)
            throws NestedChainStackOverflowException, ResolutionException, ChainNotFoundException,
            ChainChoiceException, ScopeProviderException {
        wrapped.callChainSecurely(chainName, processor);
    }

    @Override
    public void catchException(final Throwable exception, final IObject context) {
        this.exception = exception;
        this.exceptionContext = context;
    }

    @Override
    public IObject dump(final IObject options) throws DumpException, InvalidArgumentException {
        try {
            return IOC.resolve(Keys.getKeyByName("make dump"), wrapped, options);
        } catch (ResolutionException e) {
            throw new DumpException("Error creating dump of debugger sequence.", e);
        }
    }
}
