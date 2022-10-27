package info.smart_tools.smartactors.testing.test_http_endpoint;

import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;

import java.util.List;

/**
 * Implementation of {@link IChannelHandler}.
 * This implementation is needed for chain tests.
 */
public class TestChannelHandler implements IChannelHandler {

    private List<Object> responses;

    /**
     * Constructor.
     * Creates instance if {@link }
     * @param testResponses the aggregator of chain responses.
     */
    public TestChannelHandler(final List<Object> testResponses) {
        this.responses = testResponses;
    }

    @Override
    public void init(final Object channelHandler) {
        this.responses = (List<Object>) channelHandler;
    }

    @Override
    public void send(final Object response) {
        this.responses.add(response);
    }

    @Override
    public Object getHandler() {
        return null;
    }
}
