package info.smart_tools.smartactors.http_endpoint.http_client_initializer;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;

/**
 * Created by sevenbits on 07.10.16.
 */
public class HttpClientInitializerTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
    }
}
