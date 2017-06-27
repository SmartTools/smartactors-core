package info.smart_tools.smartactors.iobject_extension_plugins.wds_object_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_extension.wds_object.WDSObject;
import info.smart_tools.smartactors.iobject_extension.wds_object.WDSObjectFieldSet;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Plugin registers into IOC strategy for creation new instance of
 * {@link WDSObject} and strategy for storing other strategies that will be using as WDS transformation rules.
 */
public class PluginWDSObject extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginWDSObject(IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("wds_object_field_set_map_strategies")
    public void registerFieldSetDependencies()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getOrAdd("WDSObject field set thread safe map"),
                // TODO:: Use some lock-free hash map
                new ApplyFunctionToArgumentsStrategy(args -> new ConcurrentSkipListMap())
        );

        IOC.register(
                Keys.getOrAdd("WDSObject field set non thread safe map"),
                new ApplyFunctionToArgumentsStrategy(args -> new HashMap())
        );
    }

    @Item("wds_object_rules_strategy")
    public void registerRulesStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                new ResolveByNameIocStrategy(
                        (a) -> a[1]
                )
        );
    }

    @Item("wds_object_field_set_strategy")
    @After({
        "wds_object_field_set_map_strategies"
    })
    public void registerFieldSetCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getOrAdd("thread safe wrapper configuration"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        Map<IFieldName, IField> inMap = IOC.resolve(Keys.getOrAdd("WDSObject field set thread safe map"));
                        Map<IFieldName, IField[]> outMap = IOC.resolve(Keys.getOrAdd("WDSObject field set thread safe map"));
                        return new WDSObjectFieldSet((IObject) args[0], inMap, outMap);
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );

        IOC.register(
                Keys.getOrAdd("non thread safe wrapper configuration"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        Map<IFieldName, IField> inMap = IOC.resolve(Keys.getOrAdd("WDSObject field set non thread safe map"));
                        Map<IFieldName, IField[]> outMap = IOC.resolve(Keys.getOrAdd("WDSObject field set non thread safe map"));
                        return new WDSObjectFieldSet((IObject) args[0], inMap, outMap);
                    } catch (Exception e) {
                        throw new FunctionExecutionException(e);
                    }
                })
        );
    }

    @Item("wds_object")
    @After({
        "wds_object_field_set_strategy",
        "wds_object_rules_strategy",
    })
    public void registerWDSObjectCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getOrAdd(WDSObject.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        if (args[0] instanceof IObject) {
                            return new WDSObject((IObject) args[0]);
                        }

                        if (args[0] instanceof WDSObjectFieldSet) {
                            return new WDSObject((WDSObjectFieldSet) args[0]);
                        }
                    } catch (InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }

                    throw new FunctionExecutionException("Unexpected argument types for WDSObject creation strategy.");
                }));
    }
}
