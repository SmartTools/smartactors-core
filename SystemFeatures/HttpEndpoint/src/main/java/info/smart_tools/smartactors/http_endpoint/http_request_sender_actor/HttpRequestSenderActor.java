package info.smart_tools.smartactors.http_endpoint.http_request_sender_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.iclient.IClient;
import info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.exception.HttpRequestSenderActorException;
import info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.wrappers.HttpRequestSenderActorWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Actor for sending request by http
 * <p>
 * Request example
 * <pre>
 * "request": {
 * "uuid": "some_uuid",
 * "uri": "http://uri.for.request/something",
 * "method": "POST",
 * "timeout": 100, after this timeout to "exceptionalMessageMapId" will be sent message with full request
 * "exceptionalMessageMapId": "SelectChain",
 * "messageMapId": "sendRequest", start chain for response
 * "content": {
 * "hello": "world"
 * }
 * }
 * </pre>
 */
public class HttpRequestSenderActor {
    private IFieldName uriFieldName;

    /**
     * Constructor for actor
     */
    public HttpRequestSenderActor() {
        try {
            uriFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uri");
        } catch (ResolutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler of the actor for send response
     *
     * @param message Wrapper of the actor
     * @throws HttpRequestSenderActorException if there are some problems on sending http request
     */
    public void sendRequest(final HttpRequestSenderActorWrapper message)
            throws HttpRequestSenderActorException {
        try {
            if (message.getRequest().getValue(uriFieldName).toString().startsWith("http:")) {
                uriFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "uri");
                if (message.getRequest().getValue(uriFieldName) != null) {
                    IClient client = IOC.resolve(Keys.getKeyByName("getHttpClient"), message.getRequest());
                    IOC.resolve(Keys.getKeyByName("sendHttpRequest"), client, message.getRequest());
                }
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new HttpRequestSenderActorException(e);
        }
    }
}
