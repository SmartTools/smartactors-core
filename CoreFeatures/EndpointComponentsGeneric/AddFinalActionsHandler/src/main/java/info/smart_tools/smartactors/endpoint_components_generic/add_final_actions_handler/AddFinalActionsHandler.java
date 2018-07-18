package info.smart_tools.smartactors.endpoint_components_generic.add_final_actions_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Handler that adds actions to list of final actions for a inbound internal internal message.
 *
 * @param <TSrc>
 * @param <TCtx>
 */
public class AddFinalActionsHandler<TSrc, TCtx>
        implements IBypassMessageHandler<IDefaultMessageContext<TSrc, IObject, TCtx>> {
    private final IFieldName finalActionsFN;
    private final IFieldName contextFN;
    private final Collection<IAction<IObject>> actions;

    /**
     * The constructor.
     *
     * @param actions the actions
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public AddFinalActionsHandler(final Collection<IAction<IObject>> actions)
            throws ResolutionException {
        this.actions = actions;
        finalActionsFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "finalActions");
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IDefaultMessageContext<TSrc, IObject, TCtx>> next,
            final IDefaultMessageContext<TSrc, IObject, TCtx> context)
                throws MessageHandlerException {
        try {
            IObject env = context.getDstMessage();
            IObject ctx = (IObject) env.getValue(contextFN);
            Collection<IAction<IObject>> finalActions = (Collection) ctx.getValue(finalActionsFN);
            if (null == finalActions) {
                finalActions = new ArrayList<>(actions.size());
                ctx.setValue(finalActionsFN, finalActions);
            }
            finalActions.addAll(actions);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new MessageHandlerException(e);
        }

        next.handle(context);
    }
}
