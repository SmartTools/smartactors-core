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
import java.io.File;
import java.util.List;

public class ServerSSLContextResolutionStrategy implements IResolveDependencyStrategy {
    private final IFieldName serverCertificateFN, serverCertificateKeyFN, ciphersFN, serverCertificateKeyPasswordFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public ServerSSLContextResolutionStrategy() throws ResolutionException {
        serverCertificateFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "serverCertificate");
        serverCertificateKeyFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "serverCertificateKey");
        ciphersFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "ciphers");
        serverCertificateKeyPasswordFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "serverCertificateKeyPassword");
    }

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        IObject handlerConf = (IObject) args[0];
        IObject endpointConf = (IObject) args[1];

        try {
            File certFile = new File((String) endpointConf.getValue(serverCertificateFN));
            File keyFile = new File((String) endpointConf.getValue(serverCertificateKeyFN));
            String keyPassword = (String) endpointConf.getValue(serverCertificateKeyPasswordFN);
            List ciphers = (List) handlerConf.getValue(ciphersFN);

            SslContext context = SslContextBuilder
                    .forServer(certFile, keyFile, keyPassword)
                    .ciphers(ciphers)
                    .build();

            return (T) context;
        } catch (ReadValueException | InvalidArgumentException | SSLException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
