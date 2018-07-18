package info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;

/**
 * Default implementation of {@link IDefaultMessageContext}.
 *
 * <p>
 *  Code generation will be required when handlers of pipeline will require contexts implementing different interfaces.
 *  But as parameters held by {@link IDefaultMessageContext} are enough for all known cases this implementation should
 *  be enough yet.
 * </p>
 *
 * @param <TSrc>
 * @param <TDst>
 * @param <TCtx>
 */
public class DefaultMessageContextImplementation<TSrc, TDst, TCtx>
        implements IDefaultMessageContext<TSrc, TDst, TCtx> {
    private TSrc srcMessage;
    private TDst dstMessage;
    private TCtx conContext;

    @Override
    public TSrc getSrcMessage() {
        return srcMessage;
    }

    @Override
    public TDst getDstMessage() {
        return dstMessage;
    }

    @Override
    public TCtx getConnectionContext() {
        return conContext;
    }

    @Override
    public void setSrcMessage(final TSrc message) {
        srcMessage = message;
    }

    @Override
    public void setDstMessage(final TDst message) {
        dstMessage = message;
    }

    @Override
    public void setConnectionContext(final TCtx context) {
        conContext = context;
    }

    @Override
    public <T extends IMessageContext> T cast(final Class<? super T> hint) {
        return (T) this;
    }

    @Override
    public IMessageContext clone() {
        try {
            return (IMessageContext) super.clone();
        } catch (CloneNotSupportedException e) {
            // Impossible.
            throw new RuntimeException(e);
        }
    }
}
