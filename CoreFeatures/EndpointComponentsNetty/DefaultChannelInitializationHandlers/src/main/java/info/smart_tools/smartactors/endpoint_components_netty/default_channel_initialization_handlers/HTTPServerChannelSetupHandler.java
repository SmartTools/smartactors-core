package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Handler that appends HTTP codec and aggregator to Netty channel pipeline.
 *
 * @param <T>
 */
public class HTTPServerChannelSetupHandler<T extends IDefaultMessageContext<Channel, ?, ?>>
        implements IBypassMessageHandler<T> {
    private final int maxAggregationContentLength;

    /**
     * The constructor.
     *
     * @param maxAggregationContentLength maximal length of aggregated content
     */
    public HTTPServerChannelSetupHandler(final int maxAggregationContentLength) {
        this.maxAggregationContentLength = maxAggregationContentLength;
    }

    @Override
    public void handle(final IMessageHandlerCallback<T> next, final T context)
            throws MessageHandlerException {
        ChannelPipeline pipeline = context.getSrcMessage().pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(maxAggregationContentLength));

        next.handle(context);
    }
}
