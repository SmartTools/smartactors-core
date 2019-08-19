package info.smart_tools.smartactors.message_processing.response_sender_receiver;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.exceptions.ResponseException;

/**
 * Action sending the response to a message.
 */
public class ResponseSenderAction implements IAction<IObject> {
    private final IFieldName responseStrategyFN;
    private final IFieldName contextFN;

    public ResponseSenderAction()
            throws ResolutionException {
        responseStrategyFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responseStrategy");
        contextFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context");
    }

    @Override
    public void execute(final IObject env) throws ActionExecutionException, InvalidArgumentException {
        try {
            IObject context = (IObject) env.getValue(contextFN);
            IResponseStrategy responseStrategy = (IResponseStrategy) context.getValue(responseStrategyFN);
            responseStrategy.sendResponse(env);
        } catch (ReadValueException | InvalidArgumentException | ResponseException | NullPointerException e) {
            throw new ActionExecutionException("Error occurred sending response.", e);
        }
    }
}
