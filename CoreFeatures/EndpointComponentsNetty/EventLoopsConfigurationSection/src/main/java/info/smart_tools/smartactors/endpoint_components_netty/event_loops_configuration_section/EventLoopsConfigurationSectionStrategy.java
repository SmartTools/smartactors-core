package info.smart_tools.smartactors.endpoint_components_netty.event_loops_configuration_section;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.INettyTransportProvider;
import info.smart_tools.smartactors.endpoint_components_netty.inetty_transport_provider.exceptions.EventLoopGroupCreationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.EventLoopGroup;

import java.util.List;

/**
 * Strategy for "nettyEventLoops" section.
 *
 * <pre>
 *     "nettyEventLoops": [
 *          {
 *              "id": "serverParent",                       // Name of event loop group
 *              "transport": "prefer-native",               // Transport type
 *              "upcounter": "root",                        // Upcounter that will manage event loop shutdown
 *              "threads": 4,                               // Transport-specific parameters (thread count, etc)
 *              ...
 *          },
 *          {
 *              "id": "httpServerParent",
 *              "alias": "serverParent"                     // "httpServerParent" is a alternative name of "serverParent"
 *          },
 *          ...
 *     ]
 * </pre>
 */
public class EventLoopsConfigurationSectionStrategy implements ISectionStrategy {
    private final IFieldName sectionFieldName;
    private final IFieldName idFieldName;
    private final IFieldName transportFieldName;
    private final IFieldName aliasFieldName;
    private final IFieldName upcounterFieldName;

    /**
     * The constructor.
     *
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public EventLoopsConfigurationSectionStrategy()
            throws ResolutionException {

        sectionFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "nettyEventLoops");
        idFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "id");
        transportFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "transport");
        aliasFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "alias");
        upcounterFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "upcounter");
    }

    private void registerAliasGroup(final String id, final String aliasTo)
            throws ResolutionException, InvalidArgumentException, AdditionDependencyStrategyException {
        IAdditionDependencyStrategy groupsStrategy = IOC.resolve(
                Keys.getOrAdd("expandable_strategy#netty event loop group"));
        groupsStrategy.register(id, new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return IOC.resolve(Keys.getOrAdd("netty event loop group"), aliasTo);
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    private void registerNewGroup(final String id, final IObject groupConf)
            throws ReadValueException, ResolutionException, InvalidArgumentException,
            AdditionDependencyStrategyException {
        INettyTransportProvider transportProvider = IOC.resolve(
                Keys.getOrAdd("netty transport provider"),
                groupConf.getValue(transportFieldName));
        IUpCounter upCounter = IOC.resolve(
                Keys.getOrAdd("upcounter"),
                groupConf.getValue(upcounterFieldName));

        IAdditionDependencyStrategy groupsStrategy = IOC.resolve(
                Keys.getOrAdd("expandable_strategy#netty event loop group"));
        groupsStrategy.register(id, new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                EventLoopGroup group = transportProvider.createEventLoopGroup(groupConf);

                upCounter.onShutdownComplete(group::shutdownGracefully);

                return group;
            } catch (EventLoopGroupCreationException | UpCounterCallbackExecutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    @Override
    public void onLoadConfig(final IObject config)
            throws ConfigurationProcessingException {
        try {
            List<IObject> section = (List<IObject>) config.getValue(getSectionName());

            for (IObject loopConf : section) {
                String id = (String) loopConf.getValue(idFieldName);
                String alias = (String) loopConf.getValue(aliasFieldName);

                if (alias != null) {
                    registerAliasGroup(id, alias);
                } else {
                    registerNewGroup(id, loopConf);
                }
            }
        } catch (Exception e) {
            throw new ConfigurationProcessingException(e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return sectionFieldName;
    }
}
