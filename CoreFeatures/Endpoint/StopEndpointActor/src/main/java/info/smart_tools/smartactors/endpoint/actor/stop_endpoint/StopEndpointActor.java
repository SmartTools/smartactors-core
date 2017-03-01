package info.smart_tools.smartactors.endpoint.actor.stop_endpoint;

import info.smart_tools.smartactors.endpoint.actor.stop_endpoint.exception.StopEndpointActorException;
import info.smart_tools.smartactors.endpoint.actor.stop_endpoint.wrapper.StopEndpointWrapper;
import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class StopEndpointActor {

    public void stopEndpoint(final StopEndpointWrapper wrapper) throws StopEndpointActorException {
        try {
            String endpointName = wrapper.getEndpointName();
            IAsyncService endpointService =
                    IOC.resolve(Keys.getOrAdd("endpoints"), endpointName);
            endpointService.stop();
            IOC.resolve(Keys.getOrAdd("removeEndpoint"), endpointName);
        } catch (ReadValueException e) {
            throw new StopEndpointActorException("Error occurred loading \"endpoint\" configuration section.", e);
        } catch (ResolutionException e) {
            throw new StopEndpointActorException("Error occurred resolving \"endpoint\".", e);
        }
    }

}
