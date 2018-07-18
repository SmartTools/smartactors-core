package info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundMessageSendException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Outbound connection channel is a object representing a connection able to send outbound messages.
 *
 * <p>
 *  Outbound connection channel is created for a connection when it is able to send outbound messages not correlated
 *  with preceding inbound messages i.e. when it is not a server working in request-response mode.
 * </p>
 */
public interface IOutboundConnectionChannel {
    /**
     * Send a outbound message.
     *
     * <p>
     *  Passed message environment object should contain at least {@code "message"} field containing the message to be
     *  sent. Some connection types may require more fields.
     * </p>
     *
     * @param env outbound message environment
     * @throws OutboundMessageSendException if error occurs sending the message
     * @throws InvalidArgumentException if {@code env} is {@code null}
     * @throws InvalidArgumentException if {@code env} does not contain required fields
     */
    void send(IObject env) throws OutboundMessageSendException, InvalidArgumentException;
}
