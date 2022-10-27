package info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider;

import info.smart_tools.smartactors.https_endpoint.interfaces.issl_engine_provider.exception.SSLEngineProviderException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

import javax.net.ssl.SSLEngine;

/**
 * Interface for ssl engine provider
 */
public interface ISslEngineProvider {

    /**
     * Method for initialize ssl engine provider
     *
     * @param params Parameters for ssl engine provider
     * @throws SSLEngineProviderException if there are some problems on initialization
     */
    void init(final IObject params) throws SSLEngineProviderException;

    /**
     * Method for getting ssl engine
     *
     * @return ssl engine
     */
    SSLEngine getServerContext();

    /**
     * Method for getting ssl engine
     *
     * @return ssl engine
     */
    SSLEngine getClientContext();

    /**
     * Method for getting ssl engine
     *
     * @param hostname hostname of the server
     * @param port     port of the server
     * @return ssl engine
     */
    SSLEngine getClientContext(final String hostname, final int port);

    /**
     * Is ssl engine provider initialized
     *
     * @return true if ssl engine provider initialized and false otherwise
     */
    boolean isInitialized();


}
