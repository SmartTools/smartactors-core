package info.smart_tools.smartactors.endpoint_interfaces.iclient_callback;

import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;

/**
 * Callback that should be passed to {@link IOutboundConnectionChannel outbound channel} representing a client endpoint
 * working in request-response mode.
 *
 * <p>
 *  Callback should be passed as a field of request environment (the object passed to
 *  {@link IOutboundConnectionChannel#send(IObject)}):
 *  <pre>
 *      {
 *          "message": {
 *              // request content
 *          },
 *          "callback": {instance of {@link IClientCallback}},
 *          // ... // Protocol/implementation-specific fields
 *      }
 *  </pre>
 * </p>
 *
 * <p>
 *  For any request environment containing reference to a given instance of {@link IClientCallback callback} it's
 *  guaranteed that for each call of {@link IOutboundConnectionChannel#send(IObject)}:
 *  <ul>
 *    <li>
 *      {@link IClientCallback#onStart(IObject)} callback method will be called and
 *      {@link IOutboundConnectionChannel#send(IObject)} call will not return until corresponding
 *      {@link IClientCallback#onStart(IObject)} call returns
 *    </li>
 *    <li>
 *      One of callback methods ({@link #onSuccess(IObject, IObject)} or {@link #onError(IObject, Throwable)}) will be
 *      called (probably in another thread) after {@link #onStart(IObject)} call returns.
 *      If {@link #onStart(IObject)} call throws then the response will not be sent and none of
 *      {@link #onSuccess(IObject, IObject)} or {@link #onError(IObject, Throwable)} will be called.
 *    </li>
 *  </ul>
 *  or no interaction with callback will be performed (if outbound channel is not a request-response endpoint channel).
 * </p>
 */
public interface IClientCallback {
    /**
     * Called by endpoint when it starts sending a request.
     *
     * @param requestEnv request environment passed to {@link IOutboundConnectionChannel#send(IObject)}
     * @throws ClientCallbackException if any error occurs
     */
    void onStart(IObject requestEnv)
            throws ClientCallbackException;

    /**
     * Called by endpoint when response to request is received successfully.
     *
     * @param requestEnv request environment
     * @param response   response
     * @throws ClientCallbackException if any error occurs
     */
    void onSuccess(IObject requestEnv, IObject response)
            throws ClientCallbackException;

    /**
     * Called by endpoint when error occurs sending a request or receiving a response.
     *
     * @param requestEnv request environment
     * @param error      the error
     * @throws ClientCallbackException if any error occurs
     */
    void onError(IObject requestEnv, Throwable error)
            throws ClientCallbackException;
}
