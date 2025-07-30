package info.smart_tools.smartactors.http_endpoint.http_headers_setter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.IHeadersExtractor;
import info.smart_tools.smartactors.http_endpoint.interfaces.iheaders_extractor.exceptions.HeadersSetterException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;

/**
 * Headers setter for {@link FullHttpResponse}
 * This implementation extract headers from context of the environment and set them into response
 * Headers should presents as {@link List}&lt;{@link IObject}&gt;
 * {@code
 *     "headers": [
 *         {
 *             "name": "nameOfTheCookie",
 *             "value": "valueOfTheCookie"
 *         }
 *     ]
 * }
 */
public class HttpHeadersExtractor implements IHeadersExtractor {
    @Override
    public void set(final Object response, final IObject environment) throws HeadersSetterException {
        FullHttpResponse httpResponse = (FullHttpResponse) response;
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        IField contextField;
        IField headersField;
        IFieldName headerName;
        IFieldName headerValue;
        try {
            contextField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "context");
            headersField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "headers");
            headerName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
            headerValue = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value");
        } catch (ResolutionException e) {
            throw new HeadersSetterException("Failed to resolve fieldName", e);
        }
        IObject context = null;
        List<IObject> headers = null;
        try {
            context = contextField.in(environment, IObject.class);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new HeadersSetterException("Failed to get context from environment", e);
        }
        try {
            headers = headersField.in(context, List.class);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new HeadersSetterException("Failed to get cookies from context", e);
        }
        for (IObject header : headers) {
            try {
                httpResponse.headers().set(String.valueOf(header.getValue(headerName)),
                        String.valueOf(header.getValue(headerValue)));
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new HeadersSetterException("Failed to resolve header", e);
            }
        }
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, httpResponse.content().readableBytes());
        httpResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }
}
