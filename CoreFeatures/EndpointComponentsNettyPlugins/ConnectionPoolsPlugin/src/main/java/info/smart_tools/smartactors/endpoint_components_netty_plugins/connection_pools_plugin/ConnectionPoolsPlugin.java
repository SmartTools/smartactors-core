package info.smart_tools.smartactors.endpoint_components_netty_plugins.connection_pools_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.simple_strict_storage_strategy.SimpleStrictStorageStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_netty.default_tcp_client_connection_pool.DefaultTcpClientConnectionPool;
import info.smart_tools.smartactors.endpoint_components_netty.default_tcp_client_connection_pool.FakeTcpClientConnectionPool;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.InvalidEventLoopGroupException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.UnsupportedChannelTypeException;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPoolObserver;
import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.NullSocketConnectionPoolObserver;
import info.smart_tools.smartactors.endpoint_components_netty.read_timeout_connection_pool_observer.ReadTimeoutConnectionPoolObserver;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;

public class ConnectionPoolsPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public ConnectionPoolsPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("netty_connection_pools_storage_strategy")
    public void registerStorage() throws Exception {
        SimpleStrictStorageStrategy storage = new SimpleStrictStorageStrategy("netty client connection pool");

        /*
         * (type: String, config: IObject, setupAction: IAction<SocketChannel>) -> ISocketConnectionPool
         */
        IOC.register(Keys.getOrAdd("netty client connection pool"), storage);
        IOC.register(Keys.getOrAdd("expandable_strategy#netty client connection pool"),
                new SingletonStrategy(storage));
    }

    @Item("default_netty_connection_pool_types")
    @After({
            "netty_connection_pools_storage_strategy",
            "netty_connection_pool_observers_strategies",
    })
    public void registerDefaultPoolTypes() throws Exception {
        IFieldName transportFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "transport");
        IFieldName eventLoopGroupFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "eventLoopGroup");

        IAdditionDependencyStrategy storage = IOC.resolve(Keys.getOrAdd("expandable_strategy#netty client connection pool"));

        /*
         * {
         *   ...
         *   "transport": ".. transport name ..",
         *   "eventLoopGroup": ".. group name .."
         * }
         */
        storage.register("default",
                new ApplyFunctionToArgumentsStrategy(args -> {
                    IObject conf = (IObject) args[1];
                    IAction<SocketChannel> setupAction = (IAction) args[2];

                    try {
                        INettyTransportProvider transport = IOC.resolve(Keys.getOrAdd("netty transport provider"),
                                conf.getValue(transportFN));
                        ChannelFactory<? extends SocketChannel> channelFactory = transport.getChannelFactory(SocketChannel.class);
                        EventLoopGroup eventLoopGroup = IOC.resolve(Keys.getOrAdd("netty event loop group"),
                                conf.getValue(eventLoopGroupFN));

                        transport.verifyEventLoopGroup(eventLoopGroup);

                        ISocketConnectionPoolObserver observer = IOC.resolve(
                                Keys.getOrAdd("netty client connection pool observer"),
                                conf);

                        return new DefaultTcpClientConnectionPool(
                                channelFactory,
                                setupAction,
                                eventLoopGroup,
                                SimpleChannelPool::new,
                                observer);
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException | InvalidEventLoopGroupException
                            | UnsupportedChannelTypeException e) {
                        throw new FunctionExecutionException(e);
                    }
                }));

        /*
         * {
         *   ...
         *   "transport": ".. transport name ..",
         *   "eventLoopGroup": ".. group name .."
         * }
         */
        storage.register("none",
                new ApplyFunctionToArgumentsStrategy(args -> {
                    IObject conf = (IObject) args[1];
                    IAction<SocketChannel> setupAction = (IAction) args[2];

                    try {
                        INettyTransportProvider transport = IOC.resolve(Keys.getOrAdd("netty transport provider"),
                                conf.getValue(transportFN));
                        ChannelFactory<? extends SocketChannel> channelFactory = transport.getChannelFactory(SocketChannel.class);
                        EventLoopGroup eventLoopGroup = IOC.resolve(Keys.getOrAdd("netty event loop group"),
                                conf.getValue(eventLoopGroupFN));

                        transport.verifyEventLoopGroup(eventLoopGroup);

                        ISocketConnectionPoolObserver observer = IOC.resolve(
                                Keys.getOrAdd("netty client connection pool observer"),
                                conf);

                        return new FakeTcpClientConnectionPool(
                                channelFactory,
                                setupAction,
                                eventLoopGroup,
                                observer);
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException | InvalidEventLoopGroupException
                            | UnsupportedChannelTypeException e) {
                        throw new FunctionExecutionException(e);
                    }
                }));
    }

    @Item("netty_connection_pool_observers_strategies")
    public void registerObserverStrategies() throws Exception {
        IFieldName readTimeoutFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "readTimeout");

        IOC.register(Keys.getOrAdd("netty client connection pool observer"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    IObject conf = (IObject) args[0];

                    try {
                        Number timeout = (Number) conf.getValue(readTimeoutFN);

                        if (null != timeout) {
                            return new ReadTimeoutConnectionPoolObserver(timeout.longValue());
                        } else {
                            return NullSocketConnectionPoolObserver.get();
                        }
                    } catch (ReadValueException | InvalidArgumentException e) {
                        throw new FunctionExecutionException(e);
                    }
                }));
    }
}
