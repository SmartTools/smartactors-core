package info.smart_tools.smartactors.endpoint_components_generic.interrupt_client_callback;

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
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;

/**
 * Client callback that pauses a {@link IMessageProcessor message processor} passed within request object for the time
 * of request processing and un-pauses it when request processing is completed.
 */
public class InterruptClientCallback implements IClientCallback {
    private final IFieldName messageProcessorFN, responseFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public InterruptClientCallback()
            throws ResolutionException {
        messageProcessorFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageProcessor");
        responseFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "response");
    }

    @Override
    public void onStart(final IObject requestEnv)
            throws ClientCallbackException {
        try {
            IMessageProcessor messageProcessor = (IMessageProcessor) requestEnv.getValue(messageProcessorFN);

            if (null == messageProcessor) {
                throw new ClientCallbackException(
                        "Message processor instance should be provided for InterruptClientCallback in 'messageProcessor' " +
                                "field of request environment.");
            }

            messageProcessor.pauseProcess();
        } catch (ReadValueException | InvalidArgumentException | AsynchronousOperationException e) {
            throw new ClientCallbackException(e);
        }
    }

    @Override
    public void onSuccess(final IObject requestEnv, final IObject response)
            throws ClientCallbackException {
        IMessageProcessor messageProcessor;

        try {
            messageProcessor = (IMessageProcessor) requestEnv.getValue(messageProcessorFN);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ClientCallbackException(e);
        }

        Throwable err = null;

        try {
            messageProcessor.getMessage().setValue(responseFN, response);
        } catch (ChangeValueException | InvalidArgumentException e) {
            err = e;
        }

        try {
            messageProcessor.continueProcess(err);
        } catch (AsynchronousOperationException e) {
            throw new ClientCallbackException(e);
        }
    }

    @Override
    public void onError(final IObject requestEnv, final Throwable error)
            throws ClientCallbackException {
        try {
            IMessageProcessor messageProcessor = (IMessageProcessor) requestEnv.getValue(messageProcessorFN);

            messageProcessor.continueProcess(error);
        } catch (ReadValueException | InvalidArgumentException | AsynchronousOperationException e) {
            throw new ClientCallbackException(e);
        }
    }
}
