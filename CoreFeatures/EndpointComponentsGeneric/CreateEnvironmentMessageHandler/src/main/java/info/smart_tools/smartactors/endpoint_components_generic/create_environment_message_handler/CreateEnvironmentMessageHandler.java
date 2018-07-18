package info.smart_tools.smartactors.endpoint_components_generic.create_environment_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * A {@link IMessageHandler message handler} that creates an empty internal message environment for inbound message.
 *
 * @param <TSrc>
 * @param <TCtx>
 */
public class CreateEnvironmentMessageHandler<TSrc, TCtx>
        implements IMessageHandler<IDefaultMessageContext<TSrc, Void, TCtx>, IDefaultMessageContext<TSrc, IObject, TCtx>> {
    private final IFieldName contextFieldName, fromExternalFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public CreateEnvironmentMessageHandler()
            throws ResolutionException {
        this.contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        this.fromExternalFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "fromExternal");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<TSrc, IObject, TCtx>> next,
            final IDefaultMessageContext<TSrc, Void, TCtx> ctx)
            throws MessageHandlerException {
        try {
            IObject environment = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));
            IObject context = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

            context.setValue(fromExternalFieldName, true);

            environment.setValue(contextFieldName, context);

            IDefaultMessageContext<TSrc, IObject, TCtx> nextCtx = ctx.cast(IDefaultMessageContext.class);

            nextCtx.setDstMessage(environment);

            next.handle(nextCtx);
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }
    }
}
