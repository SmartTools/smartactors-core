package info.smart_tools.smartactors.http_endpoint.strategy.get_query_parameter;

import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetQueryParameterRuleTest {
    @Test
    public void shouldExtractRightCookie() throws Exception {
        GetQueryParameterRule rule = new GetQueryParameterRule();
        FullHttpRequest request = mock(FullHttpRequest.class);
        String uri = "/?key1=val1&key2=val2";
        when(request.getUri()).thenReturn(uri);
        assertEquals("val2", rule.resolve(request, "key2"));
    }
}
