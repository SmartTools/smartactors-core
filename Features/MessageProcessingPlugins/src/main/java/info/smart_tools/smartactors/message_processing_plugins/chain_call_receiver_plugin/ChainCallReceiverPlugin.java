package info.smart_tools.smartactors.message_processing_plugins.chain_call_receiver_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_call_receiver.ChainCallReceiver;
import info.smart_tools.smartactors.message_processing.chain_call_receiver.IChainChoiceStrategy;

/**
 * Implementation of {@link IPlugin}.
 * Plugin creates and register into IOC strategy for creation instance of
 * {@link ChainCallReceiver}.
 */
public class ChainCallReceiverPlugin implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * @param bootstrap Target bootstrap for adding strategy
     * @throws InvalidArgumentException if any errors occurred
     */
    public ChainCallReceiverPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load()
            throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("ChainCallReceiver");
            item
                    .after("IOC")
                    .before("starter")
                    .after("IFieldPlugin")
                    .after("IFieldNamePlugin")
                    .after("receiver_chains_storage")
                    .after("ChainChoiceStrategy")
                    .process(() -> {
                        try {
                            IOC.register(
                                    IOC.resolve(
                                            IOC.getKeyForKeyByNameStrategy(),
                                            ChainCallReceiver.class.getCanonicalName()
                                    ),
                                    new ApplyFunctionToArgumentsStrategy(
                                            (args) -> {
                                                try {
                                                    IFieldName fieldName = IOC.resolve(
                                                            IOC.resolve(
                                                                    IOC.getKeyForKeyByNameStrategy(),
                                                                    "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"
                                                            ),
                                                            "strategyDependency"
                                                    );
                                                    IChainChoiceStrategy strategy = IOC.resolve(
                                                            IOC.resolve(
                                                                    IOC.getKeyForKeyByNameStrategy(),
                                                                    ((IObject) args[0]).getValue(fieldName)
                                                            )
                                                    );
                                                    return new ChainCallReceiver(strategy);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                    )
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Could not create or register chain call receiver.", e
                            );
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = { ChainCallReceiver.class.getCanonicalName() };
                        Keys.unregisterByNames(keyNames);
                    });
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ChainCallReceiver plugin'", e);
        }
    }
}
