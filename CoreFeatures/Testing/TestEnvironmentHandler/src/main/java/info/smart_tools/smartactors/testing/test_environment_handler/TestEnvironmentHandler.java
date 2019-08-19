package info.smart_tools.smartactors.testing.test_environment_handler;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.testing.interfaces.iresult_checker.IResultChecker;
import info.smart_tools.smartactors.testing.test_environment_handler.exception.InvalidTestDescriptionException;

import java.util.List;

/**
 * Implementation of {@link IEnvironmentHandler}.
 * This implementation prepares and start processing test chain
 */
public class TestEnvironmentHandler implements IEnvironmentHandler {

    private static final int STACK_DEPTH = 5;

    private final IFieldName environmentFieldName;
    private final IFieldName messageFieldName;
    private final IFieldName contextFieldName;


    /**
     * The constructor.
     *
     * @throws InitializationException if creation of {@link TestEnvironmentHandler} has been failed.
     */
    public TestEnvironmentHandler()
            throws InitializationException {
        try {
            environmentFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"
            );
            messageFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message"
            );
            contextFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"
            );
        } catch (ResolutionException e) {
            throw new InitializationException("Could not create new instance of TestEnvironmentHandler.", e);
        }
    }

    @Override
    public void handle(final IObject environment, final Object receiverChainName, final IAction<Throwable> callback)
            throws EnvironmentHandleException, InvalidArgumentException {
        if (null == environment) {
            throw new InvalidArgumentException("Description should not be null.");
        }
        if (null == callback) {
            throw new InvalidArgumentException("Callback should not be null.");
        }
        if (null == receiverChainName) {
            throw new InvalidArgumentException("Receiver chain should not be null.");
        }

        try {
            IResultChecker checker = createChecker(environment);
            IMessageProcessor[] fmp = new IMessageProcessor[1];
            IObject environmentDesc = (IObject) environment.getValue(environmentFieldName);
            IObject message = (IObject) environmentDesc.getValue(messageFieldName);
            IObject context = (IObject) environmentDesc.getValue(contextFieldName);

            IAction<Throwable> completionCallback = exc -> {
                try {
                    checker.check(fmp[0], exc);

                    callback.execute(null);
                } catch (Exception e) {
                    callback.execute(e);
                }
            };
            MainTestChain mainTestChain = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), MainTestChain.class.getCanonicalName()),
                    receiverChainName,
                    completionCallback,
                    checker.getSuccessfulReceiverArguments(),
                    ScopeProvider.getCurrentScope(),
                    ModuleManager.getCurrentModule()
            );
            IMessageProcessingSequence sequence = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                    STACK_DEPTH, mainTestChain
            );
            IQueue<ITask> taskQueue = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "task_queue")
            );

            IMessageProcessor mp = IOC.resolve(
                    IOC.resolve(
                            IOC.getKeyForKeyByNameStrategy(),
                            "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"
                    ),
                    taskQueue,
                    sequence
            );
            fmp[0] = mp;
            mp.process(message, context);

        } catch (ReadValueException | ResolutionException | MessageProcessorProcessException |
                InitializationException | ScopeProviderException e) {
            throw new EnvironmentHandleException(e);
        } catch (ClassCastException e) {
            throw new EnvironmentHandleException("Could not cast value to required type.", e);
        }
    }

    private IResultChecker createChecker(final IObject description)
            throws InitializationException {
        try {
            IFieldName interceptFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "intercept"
            );
            IFieldName assertFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "assert"
            );

            List<IObject> assertions = (List<IObject>) description.getValue(assertFieldName);
            IObject intercept = (IObject) description.getValue(interceptFieldName);

            if ((null == assertions) && (null == intercept)) {
                throw new InvalidTestDescriptionException("None of \"assert\" and \"intercept\" sections are present in test description.");
            }

            if ((null != assertions) && (null != intercept)) {
                throw new InvalidTestDescriptionException("Both \"assert\" and \"intercept\" sections are present in test description.");
            }

            if (null != assertions) {
                return (IResultChecker) IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IResultChecker.class.getCanonicalName() + "#assert"), assertions
                );
            } else {
                return (IResultChecker) IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IResultChecker.class.getCanonicalName() + "#intercept"), intercept
                );
            }
        } catch (ResolutionException | InvalidTestDescriptionException | InvalidArgumentException | ReadValueException e) {
            throw new InitializationException(e);
        }
    }
}
