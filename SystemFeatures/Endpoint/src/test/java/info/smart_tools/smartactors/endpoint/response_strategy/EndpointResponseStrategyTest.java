package info.smart_tools.smartactors.endpoint.response_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_content_strategy.IResponseContentStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.exceptions.ResponseException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EndpointResponseStrategy}.
 */
public class EndpointResponseStrategyTest extends IOCInitializer {
    private IObject environment;
    private IObject context;
    private IObject responseObj;
    private IResponseSender responseSender;
    private IChannelHandler channelHandler;
    private IResponse response;
    private IResponseContentStrategy responseContentStrategy;
    private Object request;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        environment = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        context = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        responseObj = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        responseSender = mock(IResponseSender.class);
        channelHandler = mock(IChannelHandler.class);
        response = mock(IResponse.class);
        responseContentStrategy = mock(IResponseContentStrategy.class);

        environment.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"), context);
        environment.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "response"), responseObj);
        context.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "endpointName"), "theEpName");
        context.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "channel"), channelHandler);

        request = mock(Object.class);

        IStrategy strategy = mock(IStrategy.class);
        when(strategy.resolve()).thenReturn(response).thenThrow(StrategyException.class);
        IOC.register(Keys.getKeyByName(IResponse.class.getCanonicalName()), strategy);

        strategy = mock(IStrategy.class);
        when(strategy.resolve(same(environment))).thenReturn(responseContentStrategy);
        IOC.register(Keys.getKeyByName(IResponseContentStrategy.class.getCanonicalName()), strategy);

        strategy = mock(IStrategy.class);
        when(strategy.resolve(same(request), eq("theEpName"))).thenReturn(responseSender);
        IOC.register(Keys.getKeyByName(IResponseSender.class.getCanonicalName()), strategy);

        strategy = mock(IStrategy.class);
        when(strategy.resolve(same(environment))).thenReturn(request);
        IOC.register(Keys.getKeyByName("http_request_key_for_response_sender"), strategy);
    }

    @Test
    public void Should_sendResponse()
            throws Exception {
        new EndpointResponseStrategy().sendResponse(environment);

        verify(responseContentStrategy).setContent(responseObj, response);
        verify(responseSender).send(same(response), same(environment), same(channelHandler));
        assertEquals(Boolean.TRUE, context.getValue(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sendResponseOnChainEnd")));
    }

    @Test(expected = ResponseException.class)
    public void Should_wrapExceptions()
            throws Exception {
        doThrow(ResponseSendingException.class).when(responseSender).send(any(), any(), any());

        new EndpointResponseStrategy().sendResponse(environment);
    }
}
