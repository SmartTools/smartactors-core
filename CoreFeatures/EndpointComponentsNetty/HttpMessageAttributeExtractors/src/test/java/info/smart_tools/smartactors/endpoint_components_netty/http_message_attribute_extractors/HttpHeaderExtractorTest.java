package info.smart_tools.smartactors.endpoint_components_netty.http_message_attribute_extractors;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link HttpHeaderExtractor}.
 */
public class HttpHeaderExtractorTest {
    @Test public void Should_readHeader() throws Exception {
        IMessageContext context = mock(IMessageContext.class);
        IFunction<IMessageContext, FullHttpMessage> messageExtractor = mock(IFunction.class);
        String defValue = "-default-", headerName = "some-header";

        when(messageExtractor.execute(same(context))).thenReturn(mock(FullHttpMessage.class));
        when(messageExtractor.execute(context).headers()).thenReturn(mock(HttpHeaders.class));
        when(messageExtractor.execute(context).headers().get(eq(headerName), eq(defValue)))
                .thenReturn("-the-value-");

        assertEquals("-the-value-", new HttpHeaderExtractor(messageExtractor, headerName, defValue).execute(context));
    }
}
