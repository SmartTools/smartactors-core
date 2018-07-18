package info.smart_tools.smartactors.endpoint_components_generic.respond_to_chain_client_callback;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;

/**
 * Sends a internal message when response is received successfully or error occurs.
 *
 * <p>
 * When response is received a message with environment like this:
 * <pre>
 * {
 *     "message": {
 *         .. deserialized response ..
 *     },
 *     "context": {
 *         .. response environment .., including:
 *         "rawMessage": Object {}, // Raw response (if "store raw inbound message" configured)
 *         "request": {
 *             .. request environment ..
 *         },
 *         ... etc
 *     }
 * }
 * </pre>
 * will be sent to a chain which name is stored in {@code "successChain"} field of request environment.
 * </p>
 *
 * <p>
 * When error occurs a message environment like this:
 * <pre>
 * {
 *     "message": {},
 *     "context": {
 *         "exception": Throwable {},
 *         "request": {
 *             .. request environment ..
 *         }
 *     },
 *     ...
 * }
 * </pre>
 * will be sent to a chain which name is stored in {@code "errorChain"} field of request environment.
 * </p>
 */
public class RespondToChainClientCallback implements IClientCallback {
    private static final int DEFAULT_STACK_DEPTH = 5;

    private final IFieldName successChainFN;
    private final IFieldName errorChainFN;

    private final IFieldName messageFN;
    private final IFieldName contextFN;
    private final IFieldName requestFN;
    private final IFieldName exceptionFN;
    private final IFieldName stackDepthFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public RespondToChainClientCallback()
            throws ResolutionException {
        successChainFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "successChain");
        errorChainFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "errorChain");

        messageFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        requestFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
        exceptionFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "exception");
        stackDepthFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "stackDepth");
    }

    @Override
    public void onStart(final IObject requestEnv)
            throws ClientCallbackException {
        try {
            if (null == requestEnv.getValue(successChainFN)) {
                throw new ClientCallbackException("Identifier of success chain should be passed in 'successChain' field.");
            }

            if (null == requestEnv.getValue(errorChainFN)) {
                throw new ClientCallbackException("Identifier of error chain should be passed in 'errorChain' field.");
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ClientCallbackException(e);
        }
    }

    @Override
    public void onSuccess(final IObject requestEnv, final IObject response)
            throws ClientCallbackException {
        try {
            IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

            env.setValue(messageFN, response.getValue(messageFN));
            env.setValue(contextFN, response);

            send0(requestEnv.getValue(successChainFN), env, requestEnv);
        } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new ClientCallbackException(e);
        }
    }

    @Override
    public void onError(final IObject requestEnv, final Throwable error)
            throws ClientCallbackException {
        try {
            IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
            IObject msg = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
            IObject ctx = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

            env.setValue(messageFN, msg);
            env.setValue(contextFN, ctx);
            ctx.setValue(exceptionFN, error);
            ctx.setValue(requestFN, requestEnv);

            send0(requestEnv.getValue(errorChainFN), env, requestEnv);
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException | ReadValueException e) {
            throw new ClientCallbackException(e);
        }

    }

    private void send0(final Object chainName, final IObject env, final IObject reqEnv)
            throws ClientCallbackException {
        try {
            IChainStorage chainStorage = IOC.resolve(Keys.getOrAdd(IChainStorage.class.getCanonicalName()));
            Object chainId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), chainName);

            IReceiverChain chain = chainStorage.resolve(chainId);

            Number stackDepthB = (Number) reqEnv.getValue(stackDepthFN);
            int stackDepthU;

            if (null == stackDepthB) {
                stackDepthU = DEFAULT_STACK_DEPTH;
            } else {
                stackDepthU = stackDepthB.intValue();
            }

            IMessageProcessingSequence sequence = IOC.resolve(
                    Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()),
                    stackDepthU, chain
            );

            IMessageProcessor processor = IOC.resolve(
                    Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()),
                    IOC.resolve(Keys.getOrAdd("task_queue")), sequence
            );

            processor.process(
                    (IObject) env.getValue(messageFN),
                    (IObject) env.getValue(contextFN)
            );
        } catch (ResolutionException | ReadValueException | InvalidArgumentException | MessageProcessorProcessException
                | ChainNotFoundException e) {
            throw new ClientCallbackException(e);
        }
    }
}
