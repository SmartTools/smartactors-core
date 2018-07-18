package info.smart_tools.smartactors.endpoint_interfaces.imessage_handler;

/**
 * Base interface for a object containing tuple of parameters passed between {@link IMessageHandler message handlers}.
 *
 * <p>
 *  Message context is usually a plain object with setters and getters but interface may have type parameters.
 *  Cloning message context is allowed and has "shallow copy" semantics.
 * </p>
 *
 * <p>
 *  A handler calling another handler with the same context instance more than one time should should make a clone for
 *  each of those calls (except maybe one of them). This is required as some {@link IMessageHandler message handlers}
 *  may work asynchronously.
 * </p>
 */
public interface IMessageContext extends Cloneable {
    /**
     * Convert this object to another {@link IMessageContext message context} type.
     *
     * <p>
     *  This method usually returns {@code this} instance but it may also create new instance implementing required type
     *  if {@code this} does not.
     * </p>
     *
     * @param hint type erasure of {@code T}
     * @param <T>  expected type
     * @return the message context converted to {@code T}
     */
    <T extends IMessageContext> T cast(Class<? super T> hint);

    /**
     * @return shallow copy of this object
     */
    IMessageContext clone();
}
