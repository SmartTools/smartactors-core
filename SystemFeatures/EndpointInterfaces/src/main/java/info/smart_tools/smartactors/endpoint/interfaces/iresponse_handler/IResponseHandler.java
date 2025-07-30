package info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler;

import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.exception.ResponseHandlerException;

/**
 * Interface for response handlers
 */
public interface IResponseHandler<T, V> {
    void handle(T ctx, V response) throws ResponseHandlerException;
}
