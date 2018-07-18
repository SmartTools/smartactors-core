package info.smart_tools.smartactors.endpoint_components_netty.ssl_channel_initialization_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.util.List;

public class ClientSSLContextResolutionStrategy implements IResolveDependencyStrategy {
    private final IFieldName ciphersFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public ClientSSLContextResolutionStrategy()
            throws ResolutionException {
        ciphersFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "ciphers");
    }

    @Override
    public <T> T resolve(final Object... args)
            throws ResolveDependencyStrategyException {
        IObject handlerConf = (IObject) args[0];

        try {
            List ciphers = (List) handlerConf.getValue(ciphersFN);

            SslContext context = SslContextBuilder
                    .forClient()
                    .ciphers(ciphers)
                    .build();

            return (T) context;
        } catch (SSLException | ReadValueException | InvalidArgumentException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
