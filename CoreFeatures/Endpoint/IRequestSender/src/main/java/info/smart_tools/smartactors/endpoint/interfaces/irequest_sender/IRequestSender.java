package info.smart_tools.smartactors.endpoint.interfaces.irequest_sender;

import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception.RequestSenderException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for classes, that should send some requests to other servers
 */
public interface IRequestSender {
    /**
     * Method for sending request to other server
     *
     * @param request request, that should be send
     */
    void sendRequest(IObject request) throws RequestSenderException;
}
