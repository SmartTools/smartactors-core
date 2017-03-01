package info.smart_tools.smartactors.https_endpoint.https_request_sender_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iclient.IClient;
import info.smart_tools.smartactors.https_endpoint.https_request_sender_actor.exception.HttpsRequestSenderActorException;
import info.smart_tools.smartactors.https_endpoint.https_request_sender_actor.wrapper.HttpsRequestSenderActorWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * Created by sevenbits on 15.10.16.
 */
public class HttpsRequestSenderActor {

    private IFieldName uriFieldName;

    /**
     * Constructor for actor
     */
    public HttpsRequestSenderActor() {
        try {
            uriFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uri");
        } catch (ResolutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler of the actor for send response
     *
     * @param wrapper Wrapper of the actor
     * @throws HttpsRequestSenderActorException if there are some problems on sending http request
     */
    public void sendRequest(final HttpsRequestSenderActorWrapper wrapper)
            throws HttpsRequestSenderActorException {
        try {
            IObject request = wrapper.getRequest();
            if (request.getValue(uriFieldName).toString().startsWith("https:")) {
                uriFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uri");
                if (request.getValue(uriFieldName) != null) {
                    IClient client = IOC.resolve(Keys.getOrAdd("getHttpsClient"), request);
                    IOC.resolve(Keys.getOrAdd("sendHttpsRequest"), client, request);
                }
            }
            if (request.getValue(uriFieldName).toString().startsWith("http:")) {
                uriFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uri");
                if (request.getValue(uriFieldName) != null) {
                    IClient client = IOC.resolve(Keys.getOrAdd("getHttpClient"), request);
                    IOC.resolve(Keys.getOrAdd("sendHttpRequest"), client, request);
                }
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new HttpsRequestSenderActorException(e);
        }
    }
}
