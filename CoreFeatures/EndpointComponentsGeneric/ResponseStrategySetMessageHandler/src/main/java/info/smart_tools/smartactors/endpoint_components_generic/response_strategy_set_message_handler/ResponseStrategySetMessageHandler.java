package info.smart_tools.smartactors.endpoint_components_generic.response_strategy_set_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_generic.endpoint_response_strategy.EndpointResponseStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * A message handler that stores a response strategy and connection context in context of internal destination message.
 *
 * @param <TSrc>
 * @param <TCtx>
 */
public class ResponseStrategySetMessageHandler<TSrc, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<TSrc, IObject, TCtx>> {
    private final EndpointResponseStrategy<TCtx> responseStrategy;

    private final IFieldName contextFieldName;
    private final IFieldName connectionContextFieldName;
    private final IFieldName responseStrategyFieldName;

    /**
     * The constructor.
     *
     * @param responseStrategy response strategy to store in internal message context
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public ResponseStrategySetMessageHandler(
            final EndpointResponseStrategy<TCtx> responseStrategy)
                throws ResolutionException {
        this.responseStrategy = responseStrategy;

        contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        connectionContextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionContext");
        responseStrategyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseStrategy");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<TSrc, IObject, TCtx>> next,
            final IDefaultMessageContext<TSrc, IObject, TCtx> context)
                throws MessageHandlerException {
        try {
            IObject env = context.getDstMessage();
            IObject messageContext = (IObject) env.getValue(contextFieldName);

            messageContext.setValue(connectionContextFieldName, context.getConnectionContext());
            messageContext.setValue(responseStrategyFieldName, responseStrategy);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
