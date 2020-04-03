package info.smart_tools.smartactors.testing.test_http_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link TestFullHttpRequest}.
 */
public class TestFullHttpRequestTest {

    private IStrategyContainer container = new StrategyContainer();

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), this.container);
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
    }

    @Test
    public void checkCreation() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnCreation()
            throws Exception {
        IObject message = mock(IObject.class);
        new TestFullHttpRequest(message);
        fail();
    }

    @Test
    public void checkGetProtocolVersionMethod()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("protocolVersion"))).thenReturn("HTTP/1.1");
        when(message.getValue(new FieldName("keepAlive"))).thenReturn(true);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        assertEquals(request.getProtocolVersion(), new HttpVersion("HTTP/1.1", true));
        assertEquals(request.protocolVersion(), new HttpVersion("HTTP/1.1", true));
    }

    @Test
    public void checkGetUriMethod()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("uri"))).thenReturn("/some_uri");
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        assertEquals(request.getUri(), "/some_uri");
        assertEquals(request.uri(), "/some_uri");
    }

    @Test
    public void checkGetHeaders()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        IObject header1 = mock(IObject.class);
        IObject header2 = mock(IObject.class);
        List<IObject> headers = new ArrayList<IObject>(){{add(header1); add(header2);}};
        when(message.getValue(new FieldName("headers"))).thenReturn(headers);
        when(header1.getValue(new FieldName("name"))).thenReturn("header_name_1");
        when(header1.getValue(new FieldName("value"))).thenReturn("header_value_1");
        when(header2.getValue(new FieldName("name"))).thenReturn("header_name_2");
        when(header2.getValue(new FieldName("value"))).thenReturn("header_value_2");
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        HttpHeaders resultHeaders = request.headers();
        assertEquals(resultHeaders.get("header_name_1"), "header_value_1");
        assertEquals(resultHeaders.get("header_name_2"), "header_value_2");
    }

    @Test
    public void checkSetUriMethod()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.setUri("/uri");
        assertEquals(request.uri(), "/uri");
    }

    @Test
    public void checkSetMethodMethod()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.setMethod(new HttpMethod("GET"));
        assertEquals(request.method(), new HttpMethod("GET"));
    }

    @Test
    public void checkSetProtocolVersionMethod()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.setProtocolVersion(new HttpVersion("HTTP/2.2", false));
        assertEquals(request.protocolVersion(), new HttpVersion("HTTP/2.2", false));
    }

    @Test
    public void checkGetMethod()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("method"))).thenReturn("POST");
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        assertEquals(request.getMethod(), new HttpMethod("POST"));
        assertEquals(request.method(), new HttpMethod("POST"));
    }

    @Test (expected = RuntimeException.class)
    public void checkCopyMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.copy();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkDuplicateMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.duplicate();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkRetainedDuplicateMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.retainedDuplicate();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkReplaceMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.replace(null);
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkRetainMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.retain();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkReteinWithIncrementMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.retain(1);
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkTouchMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.touch();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkTouchWithArgMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.touch(new Object());
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkContentMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.content();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkTrailingHeadersMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.trailingHeaders();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkGetDecoderResultMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.getDecoderResult();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkDecoderResultMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.decoderResult();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkSetDecoderResultMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.setDecoderResult(null);
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkRefCntMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.refCnt();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkReleaseMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.release();
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void checkReleaseWithArgMethod() throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        assertNotNull(request);
        request.release(1);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnInvalidProtocolInArgument()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("protocolVersion"))).thenThrow(Exception.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnInvalidMethodInArgument()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("method"))).thenThrow(Exception.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnInvalidUriInArgument()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("uri"))).thenThrow(Exception.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnInvalidHeadersInArgument()
            throws Exception {
        initFieldNameStrategy();
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("headers"))).thenThrow(Exception.class);
        FullHttpRequest request = new TestFullHttpRequest(message);
        fail();
    }

    private void initFieldNameStrategy()
            throws Exception {
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ResolveByNameIocStrategy((a)-> {
                    try {
                        return new FieldName((String) a[0]);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not create new instance of FieldName.");
                    }
                })
        );
    }
}
