package info.smart_tools.smartactors.endpoints_netty.netty_tcp_single_port_server_endpoint;

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
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.Collections;

/**
 * Skeleton of TCP server endpoint with a single port.
 *
 * <p>
 *  This endpoint uses two event loop groups - one ("parent") for server socket and one ("child") for client connections
 *  (of course endpoint may be configured to use the only group for both kinds of sockets).
 * </p>
 *
 * <p>
 *  This endpoint uses one pipeline (configured as {@code "connectPipeline"}) that is notified on every new connection
 *  and may configure Netty channel pipeline of connection channel.
 * </p>
 *
 * <pre>
 *  {
 *      ...
 *
 *      "parentEventLoopGroup": ".. master group name ..",
 *      "childEventLoopGroup": ".. child group name ..",
 *
 *      "connectPipeline": ".. pipeline name ..",
 *
 *      "address": "host:port",
 *
 *      ...
 *  }
 * </pre>
 */
public class NettyTCPSinglePortServerEndpoint extends NettyBaseServerEndpoint {
    private EventLoopGroup parentGroup, childGroup;
    private ChannelFuture serverChannelFuture;

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
     * @throws UnsupportedChannelTypeException      if configured transport doesn't support server socket channels
     * @throws PipelineDescriptionNotFoundException if noo description found for configured pipeline name
     * @throws PipelineCreationException            if error occurs creating any of endpoint pipelines
     */
    public NettyTCPSinglePortServerEndpoint(
                final IObject config,
                final IEndpointPipelineSet pipelineSet)
            throws ReadValueException, InvalidArgumentException, ResolutionException, InvalidEventLoopGroupException,
            UnsupportedChannelTypeException, PipelineDescriptionNotFoundException, PipelineCreationException {
        super(config);
        IFieldName parentEventLoopGroupFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "parentEventLoopGroup");
        IFieldName childEventLoopGroupFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "childEventLoopGroup");
        IFieldName addressFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "address");
        IFieldName connectPipelineFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "connectPipeline");

        Object parentGroupName = config.getValue(parentEventLoopGroupFN);
        parentGroup = IOC.resolve(
                Keys.getOrAdd("netty event loop group"),
                parentGroupName);
        Object childGroupName = config.getValue(childEventLoopGroupFN);
        childGroup = IOC.resolve(
                Keys.getOrAdd("netty event loop group"),
                childGroupName);

        getTransportProvider().verifyEventLoopGroup(parentGroup);
        getTransportProvider().verifyEventLoopGroup(childGroup);

        InetSocketAddress socketAddress = AddressUtils.parseLocalAddress((String) config.getValue(addressFN));

        IEndpointPipeline<IDefaultMessageContext<Channel, Void, Channel>> connectPipeline =
                pipelineSet.getPipeline((String) config.getValue(connectPipelineFN));

        serverChannelFuture = new ServerBootstrap()
                .group(parentGroup, childGroup)
                .channelFactory(getTransportProvider().getChannelFactory(ServerSocketChannel.class))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(final Channel ch) throws Exception {
                        IDefaultMessageContext<Channel, Void, Channel> mc = connectPipeline.getContextFactory().execute();

                        mc.setSrcMessage(ch);
                        mc.setConnectionContext(ch);

                        connectPipeline.getInputCallback().handle(mc);
                    }
                })
                .bind(socketAddress);

        serverChannelFuture.addListener((ChannelFutureListener) cf -> {
            if (cf.isSuccess()) {
                getUpCounter().onShutdownComplete(this::shutdownSync);
            }
        });
    }

    @Override
    protected Iterable<ChannelFuture> getServerChannelFutures() {
        return Collections.singletonList(serverChannelFuture);
    }
}
