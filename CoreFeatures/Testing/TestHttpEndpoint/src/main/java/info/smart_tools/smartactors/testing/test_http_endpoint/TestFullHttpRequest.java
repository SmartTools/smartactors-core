package info.smart_tools.smartactors.testing.test_http_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link FullHttpRequest}.
 * This implementation is needed for chain tests.
 */
public class TestFullHttpRequest implements FullHttpRequest {

    private IFieldName protocolVersionTextFieldName;
    private IFieldName keepAliveFieldName;
    private IFieldName headersFieldName;
    private IFieldName headerNameFieldName;
    private IFieldName headerValueFieldName;
    private IFieldName methodFieldName;
    private IFieldName uriFieldName;

    private IObject request;

    private HttpHeaders parsedHeaders;
    private HttpMethod httpMethod;
    private HttpVersion httpProtocolVersion;
    private String parsedUri;

    /**
     * Constructor.
     * Creates instance of {@link TestFullHttpRequest}.
     * @param testRequest the instance of {@link IObject} with test data.
     * @throws InvalidArgumentException if any errors occurred.
     */
    public TestFullHttpRequest(final IObject testRequest) throws InvalidArgumentException {
        try {
            this.protocolVersionTextFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "protocolVersion"
            );
            this.keepAliveFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "keepAlive");
            this.headersFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "headers");
            this.headerNameFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
            this.headerValueFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value");
            this.methodFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "method");
            this.uriFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uri");

            this.request = testRequest;

            parseHeaders();
            parseMethod();
            parseProtocol();
            parseUri();
        } catch (Exception e) {
            throw new InvalidArgumentException("Could not initialize new instance of TestFullHttpRequest");
        }
    }

    @Override
    public FullHttpRequest copy() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest duplicate() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest retainedDuplicate() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest replace(final ByteBuf content) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest retain(final int increment) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest retain() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest touch() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest touch(final Object hint) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public FullHttpRequest setProtocolVersion(final HttpVersion version) {
        this.httpProtocolVersion = version;
        return this;
    }

    @Override
    public FullHttpRequest setMethod(final HttpMethod method) {
        this.httpMethod = method;
        return this;
    }

    @Override
    public FullHttpRequest setUri(final String uri) {
        this.parsedUri = uri;
        return this;
    }

    @Override
    public ByteBuf content() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public HttpMethod getMethod() {
        return this.httpMethod;
    }

    @Override
    public HttpMethod method() {
        return this.httpMethod;
    }

    @Override
    public String getUri() {
        return this.parsedUri;
    }

    @Override
    public String uri() {
        return this.parsedUri;
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return this.httpProtocolVersion;
    }

    @Override
    public HttpVersion protocolVersion() {
            return this.httpProtocolVersion;
    }

    @Override
    public HttpHeaders headers() {
        return this.parsedHeaders;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public DecoderResult getDecoderResult() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public DecoderResult decoderResult() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public void setDecoderResult(final DecoderResult result) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public int refCnt() {
        //return 0;
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public boolean release() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public boolean release(final int decrement) {
        throw new RuntimeException("Method not implemented");
    }

    private void parseProtocol()
            throws Exception {
        try {
            String protocolText = (String) this.request.getValue(this.protocolVersionTextFieldName);
            Object keepAlive = this.request.getValue(this.keepAliveFieldName);
            if (null == protocolText) {
                protocolText = "HTTP/1.1";         // Default value
            }
            if (null == keepAlive) {
                keepAlive = true;                   // Default value
            }
            this.httpProtocolVersion = new HttpVersion(
                    protocolText,
                    (boolean) keepAlive
            );
        } catch (Exception e) {
            throw new Exception("Could not parse protocol version.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseHeaders()
            throws Exception {
        try {
            this.parsedHeaders = new DefaultHttpHeaders();
            Object testHeaders = this.request.getValue(this.headersFieldName);
            if (null == testHeaders) {
                testHeaders = new ArrayList<IObject>();
            }

            for (IObject header : (List<IObject>) testHeaders) {
                this.parsedHeaders.add((String) header.getValue(this.headerNameFieldName), header.getValue(this.headerValueFieldName));
            }
        } catch (Exception e) {
            throw new Exception("Could not parse headers.", e);
        }
    }

    private void parseMethod()
            throws Exception {
        try {
            String method = (String) this.request.getValue(this.methodFieldName);
            if (null == method) {
                method = "POST";                // Default value
            }
            this.httpMethod = new HttpMethod(method);
        } catch (Exception e) {
            throw new Exception("Could not parse method.", e);
        }
    }

    private void parseUri()
            throws Exception {
        try {
            String uri = (String) this.request.getValue(this.uriFieldName);
            if (null == uri) {
                uri = "/";                // Default value
            }
            this.parsedUri = uri;
        } catch (Exception e) {
            throw new Exception("Could not parse uri.", e);
        }
    }
}
