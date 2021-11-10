package info.smart_tools.smartactors.endpoint_service_starter.endpoint_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Creates endpoints using configuration.
 * <p>
 * Expects the following configuration format:
 * </p>
 * {@code
 *   "client":  {
 *        "startChain": "mainChain",
 *        "stackDepth": 5 (temporarily)
 *   }
 * }
 */
public class ClientSectionProcessingStrategy implements ISectionStrategy {

    private final IFieldName name, startChainNameFieldName, queueFieldName, stackDepthFieldName;
    private final String schema;

    /**
     * Constructor
     *
     * @throws ResolutionException if there are problems on resolving IFieldName
     */
    public ClientSectionProcessingStrategy(final IFieldName sectionName, final String sectionSchema) throws ResolutionException {
        this.name = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "client");
        this.startChainNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "startChain");
        this.queueFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queue");
        this.stackDepthFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "stackDepth");

        this.schema = sectionSchema;
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            IObject clientObject = (IObject) config.getValue(name);
            IQueue<ITask> queue = IOC.resolve(Keys.getKeyByName("task_queue"));
            clientObject.setValue(queueFieldName, queue);
            Integer stackDepth = (Integer) clientObject.getValue(stackDepthFieldName);
            clientObject.setValue(stackDepthFieldName, stackDepth);
            IOC.register(Keys.getKeyByName("responseHandlerConfiguration"), new SingletonStrategy(clientObject));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ConfigurationProcessingException("Error occurred loading \"client\" configuration section.", e);
        } catch (ResolutionException e) {
            throw new ConfigurationProcessingException("Error occurred resolving \"endpoint\".", e);
        } catch (ChangeValueException | RegistrationException e) {
            throw new ConfigurationProcessingException("Error occurred registering \"client\".", e);
        }
    }

    @Override
    public void onRevertConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            IOC.unregister(Keys.getKeyByName("responseHandlerConfiguration"));
        } catch (DeletionException | ResolutionException e) {
            throw new ConfigurationProcessingException("Error occurred while reverting \"client\" configuration section.", e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return name;
    }

    @Override
    public String getSectionSchema() {
        return schema;
    }
}
