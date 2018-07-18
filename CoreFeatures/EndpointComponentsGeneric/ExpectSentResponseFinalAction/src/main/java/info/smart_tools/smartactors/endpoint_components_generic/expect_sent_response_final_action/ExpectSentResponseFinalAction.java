package info.smart_tools.smartactors.endpoint_components_generic.expect_sent_response_final_action;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.endpoint_components_generic.expect_sent_response_final_action.exceptions.ResponseExpectedException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Final action that executes an exceptional action if executed on message environment when the response was not sent.
 *
 * @param <TCtx>
 */
public class ExpectSentResponseFinalAction<TCtx> implements IAction<IObject> {
    private final Throwable error = new ResponseExpectedException("No response sent to inbound request.");

    private final IFieldName contextFN;
    private final IFieldName responseSentFN;
    private final IFieldName connectionContextFN;

    private final IBiAction<TCtx, Throwable> exceptionalAction;

    /**
     * The constructor.
     *
     * @param exceptionalAction the exceptional action
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public ExpectSentResponseFinalAction(final IBiAction<TCtx, Throwable> exceptionalAction)
            throws ResolutionException {
        this.exceptionalAction = exceptionalAction;
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        responseSentFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseSent");
        connectionContextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionContext");
    }

    @Override
    public void execute(final IObject env)
            throws ActionExecuteException, InvalidArgumentException {
        try {
            IObject context = (IObject) env.getValue(contextFN);
            Boolean responseSent = (Boolean) context.getValue(responseSentFN);

            if (null == responseSent || !responseSent) {
                @SuppressWarnings({"unchecked"})
                TCtx ctx = (TCtx) context.getValue(connectionContextFN);

                exceptionalAction.execute(ctx, error);
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ActionExecuteException(e);
        }
    }
}
