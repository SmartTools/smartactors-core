package info.smart_tools.smartactors.https_endpoint.https_request_sender_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Created by sevenbits on 15.10.16.
 */
public interface HttpsRequestSenderActorWrapper {
    IObject getRequest() throws ReadValueException;
}
