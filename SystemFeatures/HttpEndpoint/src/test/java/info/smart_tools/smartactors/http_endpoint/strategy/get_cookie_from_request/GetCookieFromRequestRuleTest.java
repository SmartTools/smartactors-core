package info.smart_tools.smartactors.http_endpoint.strategy.get_cookie_from_request;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void shouldReturnNullIfCookieIsNotFound() {
        GetCookieFromRequestRule rule = new GetCookieFromRequestRule();
        FullHttpRequest request = mock(FullHttpRequest.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(request.headers()).thenReturn(headers);
        when(headers.get(eq("Cookie"))).thenReturn("key1=val1;key2=val2");
        assertNull(rule.resolve(request, "key3"));
    }

    @Test
    public void shouldReturnNullIfCookiesIsEmpty() {
        GetCookieFromRequestRule rule = new GetCookieFromRequestRule();
        FullHttpRequest request = mock(FullHttpRequest.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(request.headers()).thenReturn(headers);
        when(headers.get(eq("Cookie"))).thenReturn(null);
        assertNull(rule.resolve(request, "key3"));
    }
}
