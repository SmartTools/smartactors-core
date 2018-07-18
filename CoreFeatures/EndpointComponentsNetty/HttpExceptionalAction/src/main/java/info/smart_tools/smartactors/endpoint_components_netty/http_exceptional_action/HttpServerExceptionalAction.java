package info.smart_tools.smartactors.endpoint_components_netty.http_exceptional_action;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Action to be executed by HTTP server endpoint when exception occurs parsing request or sending response.
 *
 * <p>
 *  This action sends empty response with some error code and closes connection.
 * </p>
 */
public class HttpServerExceptionalAction implements IBiAction<Channel, Throwable> {
    private final HttpResponseStatus responseStatus;

    /**
     * The constructor.
     *
     * @param responseStatus response status
     */
    public HttpServerExceptionalAction(final HttpResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    @Override
    public void execute(
            final Channel channel,
            final Throwable exception)
                throws ActionExecuteException, InvalidArgumentException {
        channel
                .writeAndFlush(new DefaultHttpResponse(HttpVersion.HTTP_1_1, responseStatus))
                .addListener(ChannelFutureListener.CLOSE);
    }
}
