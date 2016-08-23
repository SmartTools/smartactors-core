package info.smart_tools.smartactors.core.chain_testing;

import info.smart_tools.smartactors.core.chain_testing.checkers.TestResultChecker;
import info.smart_tools.smartactors.core.chain_testing.exceptions.InvalidTestDescriptionException;
import info.smart_tools.smartactors.core.chain_testing.exceptions.TestStartupException;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Runs tests on receiver chains.
 *
 * If the chain should be completed without exceptions then the description should have the following format:
 *
 * <pre>
 *     {
 *         "name": "Unique test name",                      // Unique name of the test
 *         "environment": {
 *             "message": {                                 // The message to send
 *                 "a": 10,
 *                 "b": 42
 *             },
 *             "context": {}                                // Context of the message
 *         },
 *         "chainName": "addChain",                         // Name of the chain to test
 *         "assert": [                                      // List of assertions to check
 *          {
 *              "name": "result should be correct",         // Unique name of the assertion
 *              "type": "eq",                               // Type of the assertion
 *              "value": "message/c"                        // Value to check. The value is described just like getter method of wrapper
 *                                                          // interface
 *              "to": 52,                                   // Additional argument(s) of assertion. Name(s) depend(s) on concrete assertion
 *                                                          // type
 *          }
 *         ]
 *     }
 * </pre>
 *
 * If the tested chain should be completed with exception then the description should have the following format:
 *
 * <pre>
 *     {
 *         "name": "Another test name",
 *         "environment": {
 *             "message": {
 *                 "a": 10,
 *                 "b": 0
 *             },
 *             "context": {}
 *         },
 *         "chainName": "divideChain",
 *         "intercept": {
 *             "class": "java.lang.ArithmeticException",    // Canonical name of exception class
 *             "target": "divideReceiver"                   // Id of the receiver that should throw exception (as if the "intercept" section
 *                                                          // was a description of a object)
 *         }
 *     }
 * </pre>
 */
public class TestRunner {
    private static final int STACK_DEPTH = 5;

    private final IFieldName environmentFieldName;
    private final IFieldName messageFieldName;
    private final IFieldName contextFieldName;
    private final IFieldName chainNameFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if resolution of any dependencies fails
     */
    public TestRunner()
            throws ResolutionException {
        environmentFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "environment");
        messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        chainNameFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "chainName");
    }

    /**
     * Run a test described by the given {@link IObject}.
     *
     * @param description    description of the test to run
     * @param callback       callback to call when test is completed (with {@code null} as the only argument in case of success or with
     *                       exception describing failure reasons in case of failure)
     * @throws InvalidArgumentException if {@code description} is {@code null}
     * @throws InvalidArgumentException if {@code callback} is {@code null}
     * @throws InvalidTestDescriptionException if test description has invalid format
     * @throws TestStartupException if failed to start the test
     */
    public void runTest(final IObject description, final IAction<Throwable> callback)
            throws InvalidArgumentException, InvalidTestDescriptionException, TestStartupException {
        if (null == description) {
            throw new InvalidArgumentException("Description should not be null.");
        }

        if (null == callback) {
            throw new InvalidArgumentException("Callback should not be null.");
        }

        try {
            TestResultChecker checker = TestResultChecker.createChecker(description);

            IMessageProcessor[] fmp = new IMessageProcessor[1];

            IObject environmentDesc = (IObject) description.getValue(environmentFieldName);

            IObject message = (IObject) environmentDesc.getValue(messageFieldName);
            IObject context = (IObject) environmentDesc.getValue(contextFieldName);

            String chainName = (String) description.getValue(chainNameFieldName);

            Object chainId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), chainName);

            IChainStorage chainStorage = IOC.resolve(Keys.getOrAdd(IChainStorage.class.getCanonicalName()));

            IReceiverChain testedChain = chainStorage.resolve(chainId);

            IAction<Throwable> completionCallback = exc -> {
                try {
                    checker.check(fmp[0], exc);

                    callback.execute(null);
                } catch (Exception e) {
                    callback.execute(e);
                }
            };

            MainTestChain mainTestChain = IOC.resolve(Keys.getOrAdd(MainTestChain.class.getCanonicalName()),
                    completionCallback, checker.getSuccessfulReceiverArguments());

            IMessageProcessingSequence sequence = IOC.resolve(Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()),
                    STACK_DEPTH, mainTestChain);

            sequence.callChain(testedChain);

            IQueue<ITask> taskQueue = IOC.resolve(Keys.getOrAdd("task_queue"));

            IMessageProcessor mp = IOC.resolve(Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()), taskQueue, sequence);
            fmp[0] = mp;

            mp.process(message, context);
        } catch (ReadValueException | ResolutionException | ChainNotFoundException | NestedChainStackOverflowException
                | ChangeValueException e) {
            throw new TestStartupException(e);
        } catch (ClassCastException e) {
            throw new InvalidTestDescriptionException("Could not cast value to required type.", e);
        }
    }
}
