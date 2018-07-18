package info.smart_tools.smartactors.endpoints_netty.netty_base_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.ChannelFuture;

/**
 * Base class for Netty server endpoints.
 *
 * <p>
 *  Subclasses must use a upcounter returned by {@link #getUpCounter()} to register a callback that will close all
 *  connections on shutdown completion.
 * </p>
 *
 * <pre>
 *  {
 *      ...
 *      "transport": ".. transport name ..",
 *      "upcounter": ".. upcounter name ..",
 *      ...
 *  }
 * </pre>
 */
public abstract class NettyBaseServerEndpoint {
    private final IObject config;
    private final INettyTransportProvider transportProvider;
    private final IUpCounter upCounter;

    /**
     * The constructor.
     *
     * @param config endpoint configuration
     * @throws ReadValueException if error occurs reading configuration
     * @throws InvalidArgumentException if some unexpected error occurs
     * @throws ResolutionException if error occurs resolving any dependency
     */
    protected NettyBaseServerEndpoint(final IObject config)
            throws ReadValueException, InvalidArgumentException, ResolutionException {
        this.config = config;

        Object transportProviderName = config.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "transport"));
        Object upCounterName = config.getValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "upcounter"));

        transportProvider = IOC.resolve(Keys.getOrAdd("netty transport provider"), transportProviderName);
        upCounter = IOC.resolve(Keys.getOrAdd("upcounter"), upCounterName);
    }

    protected INettyTransportProvider getTransportProvider() {
        return transportProvider;
    }

    protected IUpCounter getUpCounter() {
        return upCounter;
    }

    protected IObject getConfig() {
        return config;
    }

    protected abstract Iterable<ChannelFuture> getServerChannelFutures();

    public void shutdownSync() {
        for (ChannelFuture cf : getServerChannelFutures()) {
            cf
                    .awaitUninterruptibly()
                    .channel().close()
                    .awaitUninterruptibly();
        }
    }
}
