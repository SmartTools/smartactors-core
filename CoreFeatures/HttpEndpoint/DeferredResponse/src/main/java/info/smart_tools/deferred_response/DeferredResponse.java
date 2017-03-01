package info.smart_tools.deferred_response;

import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.IDeferredResponse;
import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.exception.DeferredResponseException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

/**
 * Deferred response.
 */
public class DeferredResponse implements IDeferredResponse {

    private final IFieldName contextFieldName;
    private final IFieldName channelFieldName;
    private final IFieldName requestFieldName;
    private final IFieldName headersFieldName;
    private final IFieldName cookiesFieldName;

    private final IChannelHandler channel;
    private final FullHttpRequest request;
    private final List<IObject> headers;
    private final List<IObject> cookies;

    public DeferredResponse(IObjectWrapper wrapper) throws DeferredResponseException {
        try {
            this.contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
            this.channelFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channel");
            this.requestFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
            this.headersFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers");
            this.cookiesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "cookies");

            IObject context = wrapper.getEnvironmentIObject(contextFieldName);

            this.channel = (IChannelHandler) context.getValue(channelFieldName);
            this.request = (FullHttpRequest) context.getValue(requestFieldName);
            this.headers = (List<IObject>) context.getValue(headersFieldName);
            this.cookies = (List<IObject>) context.getValue(cookiesFieldName);
        } catch (Exception e) {
            throw new DeferredResponseException("Can't create deferred response", e);
        }
    }

    @Override
    public void restoreResponse(IObjectWrapper wrapper) throws DeferredResponseException {
        try {
            IObject context = wrapper.getEnvironmentIObject(contextFieldName);

            context.setValue(channelFieldName, channel);
            context.setValue(requestFieldName, request);
            context.setValue(headersFieldName, headers);
            context.setValue(cookiesFieldName, cookies);
        } catch (Exception e) {
            throw new DeferredResponseException("Can't create deferred response", e);
        }
    }

}
