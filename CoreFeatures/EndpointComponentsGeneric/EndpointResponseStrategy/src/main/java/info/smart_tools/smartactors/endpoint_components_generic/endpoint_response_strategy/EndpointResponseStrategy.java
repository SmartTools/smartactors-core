package info.smart_tools.smartactors.endpoint_components_generic.endpoint_response_strategy;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.exceptions.ResponseException;

/**
 * Implementation of {@link IResponseStrategy response strategy} that sends response on request received through a
 * endpoint.
 *
 * @param <TCtx>
 */
public class EndpointResponseStrategy<TCtx> implements IResponseStrategy {
    private final IMessageHandlerCallback<IDefaultMessageContext<IObject, Void, TCtx>> responsePipelineCallback;
    private final IFunction0<IMessageContext> contextFactory;

    private final IFieldName messageFN;
    private final IFieldName responseFN;
    private final IFieldName contextFN;
    private final IFieldName connectionContextFN;
    private final IFieldName responseSentFN;

    private final IKey envKey;

    /**
     * The constructor.
     *
     * @param responsePipelineCallback callback that will send a message to response pipeline
     * @param contextFactory           function creating context instances for response pipeline
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public EndpointResponseStrategy(
            final IMessageHandlerCallback<IDefaultMessageContext<IObject, Void, TCtx>> responsePipelineCallback,
            final IFunction0<IMessageContext> contextFactory)
                throws ResolutionException {
        this.responsePipelineCallback = responsePipelineCallback;
        this.contextFactory = contextFactory;

        messageFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        responseFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "response");
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        connectionContextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionContext");
        responseSentFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseSent");

        envKey = Keys.getOrAdd(IObject.class.getCanonicalName());
    }

    @Override
    public void sendResponse(final IObject environment)
            throws ResponseException {
        try {
            IObject responseObject = (IObject) environment.getValue(responseFN);
            IObject requestContext = (IObject) environment.getValue(contextFN);

            // The code putting EndpointResponseStrategy to internal message context should ensure that it is of type
            // TCtx as required by response pipeline
            @SuppressWarnings({"unchecked"})
            TCtx connectionContext = (TCtx) requestContext.getValue(connectionContextFN);

            IObject responseEnv = IOC.resolve(envKey);

            responseEnv.setValue(messageFN, responseObject);
            responseEnv.setValue(contextFN, requestContext);

            IDefaultMessageContext<IObject, Void, TCtx> context = contextFactory.execute().cast(IDefaultMessageContext.class);

            context.setSrcMessage(responseEnv);
            context.setDstMessage(null);
            context.setConnectionContext(connectionContext);

            responsePipelineCallback.handle(context);

            requestContext.setValue(responseSentFN, Boolean.TRUE);
        } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException
                | FunctionExecutionException | MessageHandlerException e) {
            throw new ResponseException(e);
        }
    }
}
