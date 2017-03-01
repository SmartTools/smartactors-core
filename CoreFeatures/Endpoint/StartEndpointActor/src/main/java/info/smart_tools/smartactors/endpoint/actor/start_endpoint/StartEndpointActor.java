package info.smart_tools.smartactors.endpoint.actor.start_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.actor.start_endpoint.exception.StartEndpointActorException;
import info.smart_tools.smartactors.endpoint.actor.start_endpoint.wrapper.StartEndpointWrapper;
import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Actor for starting endpoint
 */
public class StartEndpointActor {

    private final IFieldName typeFieldName;
    private final IFieldName startChainNameFieldName;
    private final IFieldName queueFieldName;

    public StartEndpointActor() throws ResolutionException {
        this.typeFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "type");
        this.startChainNameFieldName =
                IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "startChain");
        this.queueFieldName =
                IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "queue");
    }


    /**
     * Method for starting endpoint at runtime
     *
     * @param wrapper wrapper of the message
     * @throws StartEndpointActorException throws if there are problems on creating endpoint
     */
    public void startEndpoint(final StartEndpointWrapper wrapper) throws StartEndpointActorException {
        try {
            IObject endpointConfiguration = wrapper.getEndpointConfiguration();
            IChainStorage chainStorage = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(),
                    IChainStorage.class.getCanonicalName()));
            IQueue<ITask> queue = IOC.resolve(Keys.getOrAdd("task_queue"));
            String type = (String) endpointConfiguration.getValue(typeFieldName);
            String startChainName = (String) endpointConfiguration.getValue(startChainNameFieldName);
            Object mapId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), startChainName);
            IReceiverChain chain = chainStorage.resolve(mapId);

            endpointConfiguration.setValue(startChainNameFieldName, chain);
            endpointConfiguration.setValue(queueFieldName, queue);
            IAsyncService endpointService =
                    IOC.resolve(Keys.getOrAdd(type + "_endpoint"), endpointConfiguration);
            endpointService.start();
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new StartEndpointActorException("Error occurred loading \"endpoint\" configuration section.", e);
        } catch (ResolutionException e) {
            throw new StartEndpointActorException("Error occurred resolving \"endpoint\".", e);
        } catch (ChainNotFoundException e) {
            throw new StartEndpointActorException("Error occurred resolving \"chain\".", e);
        } catch (ChangeValueException e) {
            e.printStackTrace();
        }
    }

}
