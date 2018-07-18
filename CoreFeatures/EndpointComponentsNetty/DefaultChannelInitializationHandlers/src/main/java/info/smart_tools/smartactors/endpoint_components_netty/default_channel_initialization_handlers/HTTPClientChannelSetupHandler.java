package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * Handler that appends HTTP client-side codec and aggregator to Netty channel pipeline.
 *
 * @param <T>
 */
public class HTTPClientChannelSetupHandler<T extends IDefaultMessageContext<Channel, ?, ?>>
        implements IBypassMessageHandler<T> {
    private final int maxAggregationContentLength;

    /**
     * The constructor.
     *
     * @param maxAggregationContentLength maximal length of aggregated response content
     */
    public HTTPClientChannelSetupHandler(final int maxAggregationContentLength) {
        this.maxAggregationContentLength = maxAggregationContentLength;
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<T> next,
            final T context)
                throws MessageHandlerException {
        ChannelPipeline pipeline = context.getSrcMessage().pipeline();

        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(maxAggregationContentLength));

        next.handle(context);
    }
}
