package info.smart_tools.smartactors.endpoint_components_netty.http_message_attribute_extractors;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import io.netty.handler.codec.http.FullHttpMessage;

/**
 * Function extracting header value from Netty HTTP message stored in message context.
 *
 * @param <TMsg> type of HTTP message
 * @param <TCtx> full type of context
 */
public class HttpHeaderExtractor<TMsg extends FullHttpMessage, TCtx extends IMessageContext>
        implements IFunction<TCtx, String> {
    private final IFunction<TCtx, TMsg> messageExtractor;
    private final String headerName, defaultValue;

    /**
     * The constructor.
     *
     * @param messageExtractor  function extracting Netty message from context
     * @param headerName        header name as string
     * @param defaultValue      default value to return when there is no such header
     */
    public HttpHeaderExtractor(final IFunction<TCtx, TMsg> messageExtractor, final String headerName, final String defaultValue) {
        this.messageExtractor = messageExtractor;
        this.headerName = headerName;
        this.defaultValue = defaultValue;
    }

    @Override
    public String execute(final TCtx ctx)
            throws FunctionExecutionException, InvalidArgumentException {
        return messageExtractor.execute(ctx).headers().get(headerName, defaultValue);
    }
}
