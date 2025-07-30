package info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender;

import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for response senders
 */
public interface IResponseSender {
    /**
     * Method for sending response to client
     *
     * @param responseObject Object with content of the response
     * @param environment    Environment of the message processor
     * @param ctx            Channel handler from endpoint
     * @throws ResponseSendingException if there are some problems on sending response
     */
    void send(final IResponse responseObject, final IObject environment, final IChannelHandler ctx)
            throws ResponseSendingException;
}
