package info.smart_tools.smartactors.message_processing.null_response_strategy;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.exceptions.ResponseException;

/**
 * Null-implementation of {@link IResponseStrategy response strategy}.
 */
public enum NullResponseStrategy implements IResponseStrategy { INSTANCE;
    @Override
    public void sendResponse(final IObject environment) throws ResponseException {
    }
}
