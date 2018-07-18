package info.smart_tools.smartactors.endpoint_components_generic.outbound_request_sender;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundMessageSendException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Object/(stateless)actor that sends a request to an external service using client endpoint.
 */
public class OutboundRequestSender {
    private final IFieldName callbackFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public OutboundRequestSender()
            throws ResolutionException {
        callbackFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "callback");
    }

    /**
     * Wrapper for {@link #sendRequest(SendRequestWrapper)}.
     */
    public interface SendRequestWrapper {
        /**
         * Request mode. Defines a {@link IClientCallback client callback} implementation that will be used.
         *
         * @return request mode
         * @throws ReadValueException if error occurs reading value
         */
        Object getRequestMode() throws ReadValueException;

        /**
         * Request environment.
         *
         * @return request environment
         * @throws ReadValueException if error occurs reading value
         */
        IObject getRequest() throws ReadValueException;

        /**
         * Identifier of outbound channel associated with required client endpoint.
         *
         * @return channel identifier
         * @throws ReadValueException if error occurs reading value
         */
        Object getChannelId() throws ReadValueException;
    }

    /**
     * Send a request.
     *
     * <p>
     *  This method modifies request object by setting required {@link IClientCallback callback}.
     * </p>
     *
     * <p>
     *  After call of this method request environment object (returned by {@link SendRequestWrapper#getRequest()}) is
     *  owned by request processing operation. I.e. it should not be modified or accessed by consequent message
     *  receivers.
     *  It also may and will contain non-serializable data (including but not only a {@link IClientCallback client
     *  callback} instance).
     * </p>
     *
     * @param message the message
     * @throws ReadValueException if error occurs reading values from wrapper
     * @throws ResolutionException if there is no callback implementation for required request mode
     * @throws ResolutionException if there is no required channel
     * @throws ChangeValueException if error occurs modifying request object
     * @throws InvalidArgumentException if some sudden error occurs
     * @throws OutboundMessageSendException if error occurs sending the request
     */
    public void sendRequest(final SendRequestWrapper message)
            throws ReadValueException, ResolutionException, ChangeValueException, InvalidArgumentException,
                   OutboundMessageSendException {
        IClientCallback clientCallback = IOC.resolve(
                Keys.getOrAdd("client callback"), message.getRequestMode());
        IObject request = message.getRequest();
        IOutboundConnectionChannel channel = IOC.resolve(
                Keys.getOrAdd("global outbound connection channel"),
                message.getChannelId());

        request.setValue(callbackFN, clientCallback);

        channel.send(request);
    }
}
