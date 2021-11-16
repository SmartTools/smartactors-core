package info.smart_tools.smartactors.message_bus_service_starter.message_bus_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_bus.message_bus_handler.MessageBusHandler;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Initializes MessageBus using configuration.
 * <p>
 * Expects the following configuration format:
 * <p>
 * <pre>
 *     {
 *         "messageBus": {
 *                 "routingChain": "mainChain",
 *                 "stackDepth": 5 (temporarily)
 *                 // . . .
 *             }
 *     }
 * </pre>
 */
public class MessageBusSectionProcessingStrategy implements ISectionStrategy {
    private final IFieldName name;
    private final IFieldName startChainNameFieldName;
    private final IFieldName stackDepthFieldName;

    private final String schema;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     * @param sectionName section name in config
     * @param schema JSON schema describing section
     */
    public MessageBusSectionProcessingStrategy(IFieldName sectionName, String schema)
            throws ResolutionException {
        this.name = sectionName;
        this.startChainNameFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "routingChain"
        );
        this.stackDepthFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "stackDepth"
        );

        this.schema = schema;
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            IObject messageBusObject = (IObject) config.getValue(name);

            IQueue<ITask> queue = IOC.resolve(Keys.getKeyByName("task_queue"));

            Integer stackDepth = Integer.valueOf(String.valueOf(messageBusObject.getValue(stackDepthFieldName)));

            String startChainName = (String) messageBusObject.getValue(startChainNameFieldName);

            IAction<IObject> finalAction = IOC.resolve(Keys.getKeyByName("send response action"));

            IMessageBusHandler handler = new MessageBusHandler(queue, stackDepth, startChainName, finalAction);

            ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), handler);
        } catch (ReadValueException | InvalidArgumentException | ScopeProviderException | ScopeException e) {
            throw new ConfigurationProcessingException("Error occurred loading \"message bus\" configuration section.", e);
        } catch (ResolutionException e) {
            throw new ConfigurationProcessingException("Error occurred resolving \"message bus\".", e);
        }
    }

    @Override
    public void onRevertConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            ScopeProvider.getCurrentScope().deleteValue(MessageBus.getMessageBusKey());
        } catch (ScopeProviderException | ScopeException e) {
            throw new ConfigurationProcessingException("Error occurred reverting \"message bus\" configuration section.", e);
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
