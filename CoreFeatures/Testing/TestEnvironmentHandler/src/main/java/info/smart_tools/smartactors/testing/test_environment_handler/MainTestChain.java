package info.smart_tools.smartactors.testing.test_environment_handler;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link IReceiverChain} used as a root chain for test messages. Calls a callback when chain execution completed.
 */
public class MainTestChain implements IReceiverChain {
    private IAction<Throwable> completionCallback;
    private IObject successReceiverArgs;
    private IObject testChainReceiverArgs;
    private AtomicBoolean isCompleted;
    private Object testChainName;
    private final IScope scope;
    private final IModule module;

    private IFieldName chainNameFieldName;

    private class TestChainRunnerReceiver implements IMessageReceiver {
        public void receive(IMessageProcessor mp)
                throws MessageReceiveException, AsynchronousOperationException {
            try {
                mp.getSequence().callChainSecurely(testChainName, mp);
            } catch (ResolutionException | ChainChoiceException | ChainNotFoundException |
                    NestedChainStackOverflowException | ScopeProviderException e) {
                throw new MessageReceiveException(e);
            }
        }

        public void dispose() { }
    }

    private TestChainRunnerReceiver testChainRunnerReceiver = new TestChainRunnerReceiver();

    private class SuccessfulReceiver implements IMessageReceiver {
        public void receive(IMessageProcessor mp)
                throws MessageReceiveException, AsynchronousOperationException {
            try {
                if (isCompleted.compareAndSet(false, true)) {
                    completionCallback.execute(null);
                }
            } catch (ActionExecutionException | InvalidArgumentException e) {
                throw new MessageReceiveException(e);
            }
        }

        public void dispose() { }
    }

    private IMessageReceiver successfulReceiver = new SuccessfulReceiver();

    /**
     * The constructor.
     *
     * @param chainName the testing chain
     * @param completionCallback    the callback that should be called when chain completes successful (with {@code null} as the only
     *                              argument) or with exception (with that exception as first argument)
     * @param successReceiverArgs   object that will e returned by {@link #getArguments(int)} for a receiver reached in case of successful
     *                              completion of a chain
     * @throws InvalidArgumentException if {@code completionCallback} is {@code null}
     * @throws InitializationException if resolution dependency for {@link IObject} was failed
     */
    public MainTestChain(final Object chainName, final IAction<Throwable> completionCallback, final IObject successReceiverArgs,
                         IScope scope, IModule module)
            throws InvalidArgumentException, InitializationException {
        if (null == completionCallback) {
            throw new InvalidArgumentException("Callback should not be null.");
        }
        if (null == chainName) {
            throw new InvalidArgumentException("Test chain should not be null.");
        }
        this.testChainName = chainName;
        this.completionCallback = completionCallback;
        this.successReceiverArgs = successReceiverArgs;
        this.isCompleted = new AtomicBoolean(false);
        try {
            this.chainNameFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
            this.testChainReceiverArgs = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"));
            if (null == this.successReceiverArgs) {
                this.successReceiverArgs = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"));
            }
        } catch (ResolutionException e) {
            throw new InitializationException("Could not resolve dependency for IObject.", e);
        }
        this.scope = scope;
        this.module = module;
    }

    @Override
    public IMessageReceiver get(final int index) {
        if (index == 0) {
            return this.testChainRunnerReceiver;
        }
        if (index == 1) {
            return this.successfulReceiver;
        }
        return null;
    }

    @Override
    public IObject getArguments(final int index) {
            return (index == 0) ?
                    this.testChainReceiverArgs :
                    this.successReceiverArgs;
    }

    @Override
    public Object getId() {
        return "root test chain";
    }

    @Override
    public Object getName() {
        return "root test chain";
    }

    @Override
    public IScope getScope() {
        return scope;
    }

    @Override
    public IModule getModule() {
        return module;
    }

    @Override
    public IObject getExceptionalChainNamesAndEnvironments(final Throwable exception) {
        IObject exceptionalChainAndEnv = null;
        try {
            exceptionalChainAndEnv = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"));
            exceptionalChainAndEnv.setValue(this.chainNameFieldName, new ExceptionalTestChain());
            if (isCompleted.compareAndSet(false, true)) {
                completionCallback.execute(exception);
            }
        } catch (ActionExecutionException | InvalidArgumentException | ResolutionException | ChangeValueException e) {
            e.printStackTrace();
        }



        return exceptionalChainAndEnv;
    }

    @Override
    public IObject getChainDescription() {
        return null;
    }

    @Override
    public Collection<Object> getExceptionalChainNames() {
        return Collections.emptyList();
    }
}
