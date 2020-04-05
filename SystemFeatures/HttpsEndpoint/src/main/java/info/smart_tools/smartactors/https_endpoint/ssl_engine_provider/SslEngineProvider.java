package info.smart_tools.smartactors.https_endpoint.ssl_engine_provider;

import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.ISslEngineProvider;
import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.exception.SSLEngineProviderException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;

/**
 * Class for getting ssl engine
 */
public class SslEngineProvider implements ISslEngineProvider {

    private SslContext sslServerContext = null;
    private SslContext sslClientContext = null;
    private String certKey;
    private String certPath;
    private boolean initialized = false;

    private String[] supportedCiphers = new String[]{
            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
            "TLS_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_RSA_WITH_AES_128_CBC_SHA",
            "TLS_RSA_WITH_AES_256_CBC_SHA",
            "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
            "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA"
    };

    @Override
    public void init(final IObject params) throws SSLEngineProviderException {
        IFieldName certPassFieldName = null;
        IFieldName certPathFieldName = null;
        try {
            certPassFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "certPass");
            certPathFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "certPath");
        } catch (ResolutionException e) {
            throw new SSLEngineProviderException("An exception on resolving \"FieldName\"", e);
        }
        if (params != null) {
            try {
                certKey = (String) params.getValue(certPassFieldName);
                certPath = (String) params.getValue(certPathFieldName);
                initialized = certKey != null && certPath != null;
            } catch (Exception e) {
                throw new SSLEngineProviderException("An exception on getting values from parameters", e);
            }
        }
        try {
            if (initialized) {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(new File(certPath), new File(certKey));
                sslServerContext = sslContextBuilder.ciphers(Arrays.asList(supportedCiphers)).build();
            }
            SslContextBuilder sslClientContextBuilder = SslContextBuilder.forClient().clientAuth(ClientAuth.OPTIONAL);
            sslClientContext = sslClientContextBuilder.sslProvider(SslProvider.JDK).ciphers(Arrays.asList(supportedCiphers)).build();
        } catch (SSLException e) {
            throw new SSLEngineProviderException("An exception on building ssl context", e);
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public SSLEngine getServerContext() {
        return sslServerContext.newEngine(ByteBufAllocator.DEFAULT);
    }

    @Override
    public SSLEngine getClientContext(final String hostname, final int port) {
        SSLEngine engine = sslClientContext.newEngine(ByteBufAllocator.DEFAULT, hostname, port);
        engine.setEnabledCipherSuites(supportedCiphers);
        engine.setUseClientMode(true);
        return engine;
    }
    @Override
    public SSLEngine getClientContext() {
        SSLEngine engine = sslClientContext.newEngine(ByteBufAllocator.DEFAULT);
        engine.setEnabledCipherSuites(supportedCiphers);
        engine.setUseClientMode(true);
        return engine;
    }
}