package info.smart_tools.smartactors.endpoint_components_generic.outbound_message_sender;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
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
 * Object/(stateless)actor that sends outbound messages using globally registered {@link IOutboundConnectionChannel
 * outbound connection channels}.
 */
public class OutboundMessageSender {
    private final IFieldName messageFN;
    private final IFieldName contextFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public OutboundMessageSender() throws ResolutionException {
        messageFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
    }

    /**
     * Wrapper for {@link #send(SendWrapper)}.
     */
    public interface SendWrapper {
        /**
         * @return message body
         * @throws ReadValueException if any error occurs
         */
        IObject getMessage() throws ReadValueException;

        /**
         * @return message context
         * @throws ReadValueException if any error occurs
         */
        IObject getContext() throws ReadValueException;

        /**
         * @return connection identifier
         * @throws ReadValueException if any error occurs
         */
        Object getConnectionId() throws ReadValueException;
    }

    /**
     * Sends a message.
     *
     * <p>
     *  This method builds a simple outbound message environment containing supplied message body and message context.
     * </p>
     *
     * @param message message wrapper
     * @throws OutboundMessageSendException if error occurs sending a message
     * @throws InvalidArgumentException if a unexpected error occurs
     * @throws ReadValueException if error occurs reading values from wrapper
     * @throws ChangeValueException if error occurs building outbound environment
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public void send(final SendWrapper message)
            throws OutboundMessageSendException, InvalidArgumentException, ReadValueException, ChangeValueException,
                ResolutionException {
        IOutboundConnectionChannel channel = IOC.resolve(
                Keys.getOrAdd("global outbound connection channel"),
                message.getConnectionId());

        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        env.setValue(messageFN, message.getMessage());
        env.setValue(contextFN, message.getContext());

        channel.send(env);
    }

    /**
     * Wrapper for {@link #sendEnv(SendEnvWrapper)}.
     */
    public interface SendEnvWrapper {
        /**
         * @return outbound message environment
         * @throws ReadValueException if any error occurs
         */
        IObject getEnvironment() throws ReadValueException;

        /**
         * @return connection identifier
         * @throws ReadValueException if any error occurs
         */
        Object getConnectionId() throws ReadValueException;
    }


    /**
     * Sends a message.
     *
     * <p>
     *  This method expects a full outbound message environment to be supplied through a message wrapper.
     * </p>
     *
     * @param message message wrapper
     * @throws OutboundMessageSendException if error occurs sending a message
     * @throws InvalidArgumentException if a unexpected error occurs
     * @throws ReadValueException if error occurs reading values from wrapper
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public void sendEnv(final SendEnvWrapper message) throws ResolutionException, ReadValueException,
            OutboundMessageSendException, InvalidArgumentException {
        IOutboundConnectionChannel channel = IOC.resolve(
                Keys.getOrAdd("global outbound connection channel"),
                message.getConnectionId());

        channel.send(message.getEnvironment());
    }
}
