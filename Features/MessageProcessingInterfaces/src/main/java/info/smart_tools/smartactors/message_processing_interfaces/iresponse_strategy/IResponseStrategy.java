package info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.exceptions.ResponseException;

/**
 * Interface for a strategy of sending a response to a request (message).
 *
 * Instance a class implementing this interface should be placed in message context by a service sending the message (e.g. endpoint, message
 * bus). A instance of such strategy should always be present in message context, if the message sender does not require a response to the
 * message or if the response is already sent and the strategy is not idempotent the context should contain a null-implementation of
 * response strategy.
 *
 * The name of field in message context that contains instance of response strategy should be resolved using {@code "responseStrategy"}
 * string.
 */
public interface IResponseStrategy {
    /**
     * Send response to a message.
     *
     * @param environment    message environment
     * @throws ResponseException if any error occurs sending the response
     */
    void sendResponse(IObject environment) throws ResponseException;
}
