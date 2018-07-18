package info.smart_tools.smartactors.endpoint_components_netty.http_message_attribute_extractors;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link HttpRequestMethodExtractor}
 */
public class HttpRequestMethodExtractorTest {
    @Test public void Should_extractHttpRequestMethod() throws Exception {
        IMessageContext context = mock(IMessageContext.class);
        IFunction<IMessageContext, HttpRequest> messageExtractor = mock(IFunction.class);

        when(messageExtractor.execute(same(context))).thenReturn(mock(HttpRequest.class));
        when(messageExtractor.execute(context).method()).thenReturn(HttpMethod.PATCH);

        assertEquals("PATCH", new HttpRequestMethodExtractor(messageExtractor).execute(context));
    }
}
