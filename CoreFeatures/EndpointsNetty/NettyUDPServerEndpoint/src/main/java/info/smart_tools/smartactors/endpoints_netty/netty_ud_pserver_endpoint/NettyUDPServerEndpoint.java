package info.smart_tools.smartactors.endpoints_netty.netty_ud_pserver_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.InvalidEventLoopGroupException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.UnsupportedChannelTypeException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoints_netty.netty_base_endpoint.AddressUtils;
import info.smart_tools.smartactors.endpoints_netty.netty_base_endpoint.NettyBaseServerEndpoint;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.util.Collections;

/**
 * Skeleton of UDP server endpoint listening a single port.
 *
 * <p>
 *  This endpoint uses a single event loop group.
 * </p>
 *
 * <p>
 *  This endpoint uses one pipeline (configured as {@code "setupPipeline"}) that is notified when a datagram socket is
 *  initialized.
 * </p>
 *
 * <pre>
 *  {
 *      ...
 *
 *      "eventLoopGroup": ".. event loop group name ..",
 *
 *      "setupPipeline": ".. pipeline name ..",
 *
 *      "address": "host:port",
 *
 *      ...
 *  }
 * </pre>
 */
public class NettyUDPServerEndpoint extends NettyBaseServerEndpoint {
    private EventLoopGroup eventLoopGroup;
    private ChannelFuture datagramChannelFuture;

    /**
     * The constructor.
     *
     * @param config        endpoint configuration
     * @param pipelineSet   {@link IEndpointPipelineSet pipeline set} of the endpoint
     * @throws ReadValueException                   if error occurs reading configuration
     * @throws InvalidArgumentException             if some unexpected error occurs
     * @throws ResolutionException                  if error occurs resolving any dependency
     * @throws InvalidEventLoopGroupException       if configured event loop group is not compatible with configured
     *                                              transport
     * @throws UnsupportedChannelTypeException      if configured transport doesn't support datagram channels
     * @throws PipelineDescriptionNotFoundException if noo description found for configured pipeline name
     * @throws PipelineCreationException            if error occurs creating any of endpoint pipelines
     */
    public NettyUDPServerEndpoint(final IObject config, final IEndpointPipelineSet pipelineSet)
            throws ReadValueException, InvalidArgumentException, ResolutionException, InvalidEventLoopGroupException,
            UnsupportedChannelTypeException, PipelineDescriptionNotFoundException, PipelineCreationException {
        super(config);

        IFieldName eventLoopGroupFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "eventLoopGroup");
        IFieldName setupPipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "setupPipeline");
        IFieldName addressFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "address");

        Object eventLoopGroupName = config.getValue(eventLoopGroupFN);
        eventLoopGroup = IOC.resolve(Keys.getOrAdd("netty event loop group"), eventLoopGroupName);

        getTransportProvider().verifyEventLoopGroup(eventLoopGroup);

        InetSocketAddress address = AddressUtils.parseLocalAddress((String) config.getValue(addressFN));

        IEndpointPipeline<IDefaultMessageContext<DatagramChannel, Void, DatagramChannel>> inboundDatagramPipeline =
                pipelineSet.getPipeline((String) config.getValue(setupPipelineFN));

        Bootstrap bootstrap = new Bootstrap()
                .channelFactory(getTransportProvider().getChannelFactory(DatagramChannel.class))
                .group(eventLoopGroup)
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(final DatagramChannel ch) throws Exception {
                        IDefaultMessageContext<DatagramChannel, Void, DatagramChannel> mc =
                                inboundDatagramPipeline.getContextFactory().execute();

                        mc.setSrcMessage(ch);
                        mc.setConnectionContext(ch);

                        inboundDatagramPipeline.getInputCallback().handle(mc);
                    }
                });

        datagramChannelFuture = bootstrap.bind(address);

        datagramChannelFuture.addListener((ChannelFutureListener) cf -> {
            if (cf.isSuccess()) {
                getUpCounter().onShutdownComplete(this::shutdownSync);
            }
        });
    }

    @Override
    protected Iterable<ChannelFuture> getServerChannelFutures() {
        return Collections.singletonList(datagramChannelFuture);
    }
}
