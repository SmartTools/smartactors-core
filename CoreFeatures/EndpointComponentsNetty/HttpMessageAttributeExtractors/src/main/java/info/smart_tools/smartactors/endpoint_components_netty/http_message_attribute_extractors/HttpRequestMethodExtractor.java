package info.smart_tools.smartactors.endpoint_components_netty.http_message_attribute_extractors;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Function that extracts Netty HTTP request method name from request stored in message context.
 *
 * @param <TReq> full request type
 * @param <TCtx> full type of context
 */
public class HttpRequestMethodExtractor<TReq extends HttpRequest, TCtx extends IMessageContext>
        implements IFunction<TCtx, String> {
    private final IFunction<TCtx, TReq> messageExtractor;

    /**
     * The constructor.
     *
     * @param messageExtractor function that extracts a Netty HTTP request from message context
     */
    public HttpRequestMethodExtractor(final IFunction<TCtx, TReq> messageExtractor) {
        this.messageExtractor = messageExtractor;
    }

    @Override
    public String execute(final TCtx ctx)
            throws FunctionExecutionException, InvalidArgumentException {
        return messageExtractor.execute(ctx).method().name();
    }
}
