package info.smart_tools.smartactors.transformation_rules.get_cookie_from_request;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class GetCookieFromRequestRuleTest {
    @Test
    public void shouldExtractRightCookie() {
        GetCookieFromRequestRule rule = new GetCookieFromRequestRule();
        FullHttpRequest request = mock(FullHttpRequest.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(request.headers()).thenReturn(headers);
        when(headers.get(eq("Cookie"))).thenReturn("key1=val1;key2=val2");
        assertEquals("val2", rule.resolve(request, "key2"));
    }
}
