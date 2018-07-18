package info.smart_tools.smartactors.endpoint_components_generic.null_client_callback;

import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * {@link IClientCallback Client callback} implementation that does nothing.
 *
 * <p>
 *  Useful for cases when a request is sent in "shoot and forget" mode.
 * </p>
 */
public class NullClientCallback implements IClientCallback {
    @Override
    public void onStart(IObject requestEnv) throws ClientCallbackException {
    }

    @Override
    public void onSuccess(IObject requestEnv, IObject response) throws ClientCallbackException {
    }

    @Override
    public void onError(IObject requestEnv, Throwable error) throws ClientCallbackException {
    }
}
