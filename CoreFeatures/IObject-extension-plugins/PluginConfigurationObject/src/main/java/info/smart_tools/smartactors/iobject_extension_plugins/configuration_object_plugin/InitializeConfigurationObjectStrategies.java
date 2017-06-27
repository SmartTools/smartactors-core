package info.smart_tools.smartactors.iobject_extension_plugins.configuration_object_plugin;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.iobject_extension.configuration_object.CObjectStrategy;
import info.smart_tools.smartactors.iobject_extension.configuration_object.ConfigurationObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * creates and register some strategies for correct work of
 * {@link ConfigurationObject}.
 */
public class InitializeConfigurationObjectStrategies implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public InitializeConfigurationObjectStrategies(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("ConfigurationObject");
            item
                    .after("IOC")
                    .process(
                            () -> {
                                try {
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "configuration object"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {

                                                        if (a.length == 0) {
                                                            return new ConfigurationObject();
                                                        } else if (a.length == 1 && a[0] instanceof String) {
                                                            try {
                                                                return new ConfigurationObject((String) a[0]);
                                                            } catch (InvalidArgumentException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        } else {
                                                            throw new RuntimeException("Could not create new instance of Configuration Object");
                                                        }
                                                    }
                                            )
                                    );
//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "configuration object default strategy"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            return a[0];
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(
//                                                                    "Error in configuration 'default' rule.", e
//                                                            );
//                                                        }
//                                                    }
//                                            )
//                                    );

                                    IResolveDependencyStrategy defaultStrategy = new ApplyFunctionToArgumentsStrategy(
                                            (a) -> {
                                                try {
                                                    return a[1];
                                                } catch (Throwable e) {
                                                    throw new RuntimeException(
                                                            "Error in configuration 'default' rule: " + e.getMessage(), e
                                                    );
                                                }
                                            }
                                    );

//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "configuration object in_ strategy"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            Object obj = a[0];
//                                                            if (obj instanceof String) {
//                                                                IObject innerObject = new ConfigurationObject();
//                                                                innerObject.setValue(new FieldName("name"), "wds_getter_strategy");
//                                                                innerObject.setValue(new FieldName("args"), new ArrayList<String>() {{ add((String) obj); }});
//
//                                                                return new ArrayList<IObject>() {{ add(innerObject); }};
//                                                            }
//                                                            return obj;
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(
//                                                                    "Error in configuration 'wrapper' rule.", e
//                                                            );
//                                                        }
//                                                    }
//                                            )
//                                    );
                                    IResolveDependencyStrategy inStrategy = new ApplyFunctionToArgumentsStrategy(
                                            (a) -> {
                                                try {
                                                    Object obj = a[1];
                                                    if (obj instanceof String) {
                                                        IObject innerObject = new ConfigurationObject();
                                                        innerObject.setValue(new FieldName("name"), "wds_getter_strategy");
                                                        innerObject.setValue(new FieldName("args"), new ArrayList<String>() {{ add((String) obj); }});

                                                        return new ArrayList<IObject>() {{ add(innerObject); }};
                                                    }
                                                    return obj;
                                                } catch (Throwable e) {
                                                    throw new RuntimeException(
                                                            "Error in configuration 'wrapper' rule: " + e.getMessage(), e
                                                    );
                                                }
                                            }
                                    );
//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "configuration object out_ strategy"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            Object obj = a[0];
//                                                            if (obj instanceof String) {
//                                                                IObject innerObject = new ConfigurationObject();
//                                                                innerObject.setValue(new FieldName("name"), "wds_target_strategy");
//                                                                innerObject.setValue(new FieldName("args"), new ArrayList<String>() {{ add("local/value"); add((String) obj); }});
//
//                                                                return new ArrayList<List<IObject>>() {{
//                                                                    add(new ArrayList<IObject>() {{  add(innerObject); }});
//                                                                }};
//                                                            }
//                                                            if (obj instanceof List) {
//                                                                for (Object o : (List) obj) {
//                                                                    if (o instanceof List) {
//                                                                        for (Object innerObject : (List) o) {
//                                                                            if (((IObject) innerObject).getValue(new FieldName("name")).equals("target")) {
//                                                                                ((IObject) innerObject).setValue(new FieldName("name"), "wds_target_strategy");
//                                                                                ((IObject) innerObject).setValue(new FieldName("args"), new ArrayList<String>() {{
//                                                                                            add("local/value");
//                                                                                            add((String) ((List) ((IObject) innerObject)
//                                                                                                    .getValue(new FieldName("args"))).get(0));
//                                                                                        }}
//                                                                                );
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                            return obj;
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException("Error in configuration 'wrapper' rule.", e);
//                                                        }
//                                                    }
//                                            )
//                                    );
                                    IResolveDependencyStrategy outStrategy = new ApplyFunctionToArgumentsStrategy(
                                            (a) -> {
                                                try {
                                                    Object obj = a[1];
                                                    if (obj instanceof String) {
                                                        IObject innerObject = new ConfigurationObject();
                                                        innerObject.setValue(new FieldName("name"), "wds_target_strategy");
                                                        innerObject.setValue(new FieldName("args"), new ArrayList<String>() {{ add("local/value"); add((String) obj); }});

                                                        return new ArrayList<List<IObject>>() {{
                                                            add(new ArrayList<IObject>() {{  add(innerObject); }});
                                                        }};
                                                    }
                                                    if (obj instanceof List) {
                                                        for (Object o : (List) obj) {
                                                            if (o instanceof List) {
                                                                for (Object innerObject : (List) o) {
                                                                    if (((IObject) innerObject).getValue(new FieldName("name")).equals("target")) {
                                                                        ((IObject) innerObject).setValue(new FieldName("name"), "wds_target_strategy");
                                                                        ((IObject) innerObject).setValue(new FieldName("args"), new ArrayList<String>() {{
                                                                                    add("local/value");
                                                                                    add((String) ((List) ((IObject) innerObject)
                                                                                            .getValue(new FieldName("args"))).get(0));
                                                                                }}
                                                                        );
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    return obj;
                                                } catch (Throwable e) {
                                                    throw new RuntimeException("Error in configuration 'wrapper' rule: " + e.getMessage(), e);
                                                }
                                            }
                                    );
//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "configuration object exceptional strategy"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            Object obj = a[0];
//                                                            if (obj instanceof List) {
//                                                                for (IObject innerObject : (List<IObject>) obj) {
//                                                                    if (null == innerObject.getValue(new FieldName("after"))) {
//                                                                        innerObject.setValue(new FieldName("after"), "break");
//                                                                    }
//                                                                }
//                                                            }
//                                                            return obj;
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(
//                                                                    "Error in configuration 'exceptional' rule.", e
//                                                            );
//                                                        }
//                                                    }
//                                            )
//                                    );
                                    IResolveDependencyStrategy exceptionalStrategy = new ApplyFunctionToArgumentsStrategy(
                                            (a) -> {
                                                try {
                                                    Object obj = a[1];
                                                    if (obj instanceof List) {
                                                        for (IObject innerObject : (List<IObject>) obj) {
                                                            if (null == innerObject.getValue(new FieldName("after"))) {
                                                                innerObject.setValue(new FieldName("after"), "break");
                                                            }
                                                        }
                                                    }
                                                    return obj;
                                                } catch (Throwable e) {
                                                    throw new RuntimeException(
                                                            "Error in configuration 'exceptional' rule: " + e.getMessage(), e
                                                    );
                                                }
                                            }
                                    );

//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "configuration object canonical maps strategy"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            Object obj = a[0];
//
//                                                            obj = IOC.resolve(
//                                                                    IOC.resolve(
//                                                                            IOC.getKeyForKeyStorage(),
//                                                                            "configuration object maps strategy"
//                                                                            ),
//                                                                    obj
//                                                            );
//
//                                                            if (obj instanceof List) {
//                                                                for (IObject innerObject : (List<IObject>) obj) {
//                                                                    if (null == innerObject.getValue(new FieldName("externalAccess"))) {
//                                                                        innerObject.setValue(new FieldName("externalAccess"), false);
//                                                                    }
//                                                                    if (!innerObject.getValue(new FieldName("id")).equals("tryToTakeResourceMap")) {
//                                                                        List exceptionalList = (List) innerObject.getValue(new FieldName("exceptional"));
//
//                                                                        IObject outOfResourcesExceptionObj = new ConfigurationObject();
//                                                                        outOfResourcesExceptionObj.setValue(new FieldName("class"), "info.smart_tools.smartactors.base.interfaces.iresource_source.exceptions.OutOfResourceException");
//                                                                        outOfResourcesExceptionObj.setValue(new FieldName("chain"), "tryToTakeResourceMap");
//                                                                        outOfResourcesExceptionObj.setValue(new FieldName("after"), "break");
//                                                                        exceptionalList.add(0, outOfResourcesExceptionObj);
//                                                                    }
//                                                                }
//                                                            }
//                                                            return obj;
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(
//                                                                    "Error in configuration 'canonical maps' rule.", e
//                                                            );
//                                                        }
//                                                    }
//                                            )
//                                    );
                                    IResolveDependencyStrategy mapsStrategy = new ApplyFunctionToArgumentsStrategy(
                                            (a) -> {
                                                try {
                                                    Object obj = a[1];

//                                                    obj = IOC.resolve(
//                                                            IOC.resolve(
//                                                                    IOC.getKeyForKeyStorage(),
//                                                                    "configuration object maps strategy"
//                                                                    ),
//                                                            obj
//                                                    );

                                                    if (obj instanceof List) {
                                                        for (IObject innerObject : (List<IObject>) obj) {
                                                            if (null == innerObject.getValue(new FieldName("externalAccess"))) {
                                                                innerObject.setValue(new FieldName("externalAccess"), false);
                                                            }
                                                            if (!innerObject.getValue(new FieldName("id")).equals("tryToTakeResourceMap")) {
                                                                List exceptionalList = (List) innerObject.getValue(new FieldName("exceptional"));
                                                                if (exceptionalList == null) {
                                                                    // TODO: may be set default empty value?
                                                                    throw new Exception("Field 'exceptional' is missed.");
                                                                }

                                                                IObject outOfResourcesExceptionObj = new ConfigurationObject();
                                                                outOfResourcesExceptionObj.setValue(new FieldName("class"), "info.smart_tools.smartactors.base.interfaces.iresource_source.exceptions.OutOfResourceException");
                                                                outOfResourcesExceptionObj.setValue(new FieldName("chain"), "tryToTakeResourceMap");
                                                                outOfResourcesExceptionObj.setValue(new FieldName("after"), "break");
                                                                exceptionalList.add(0, outOfResourcesExceptionObj);
                                                            }
                                                        }
                                                    }
                                                    return obj;
                                                } catch (Throwable e) {
                                                    throw new RuntimeException(
                                                            "Error in configuration 'canonical maps' rule: " + e.getMessage(), e
                                                    );
                                                }
                                            }
                                    );

//                                    final String checkpointWrapperConfig = ("{" +
//                                            "'in_getProcessor':'processor'," +
//                                            "'in_getMessage':'message'," +
//                                            "'in_getCheckpointId':'arguments/id'," +
//                                            "'in_getSchedulingConfiguration':'arguments/scheduling'," +
//                                            "'in_getRecoverConfiguration':'arguments/recover'," +
//                                            "'in_getCheckpointStatus':'message/checkpointStatus'," +
//                                            "'out_setCheckpointStatus':'message/checkpointStatus'" +
//                                            "}")
//                                            .replace('"', '\'');
//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "checkpoint step"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    a -> {
//                                                        try {
//                                                            IObject checkpointConfig = (IObject) a[0];
//
//                                                            IFieldName targetFieldName = new FieldName("target");
//                                                            IFieldName handlerFieldName = new FieldName("handler");
//                                                            IFieldName wrapperFieldName = new FieldName("wrapper");
//
//                                                            checkpointConfig.setValue(targetFieldName, "checkpoint");
//                                                            checkpointConfig.setValue(handlerFieldName, "enter");
//
//                                                            checkpointConfig.setValue(wrapperFieldName, IOC.resolve(
//                                                                    IOC.resolve(IOC.getKeyForKeyStorage(), "configuration object"),
//                                                                    checkpointWrapperConfig
//                                                            ));
//
//                                                            return checkpointConfig;
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(e);
//                                                        }
//                                                    }
//                                            )
//                                    );
//                                    IOC.register(
//                                            IOC.resolve(
//                                                    IOC.getKeyForKeyStorage(), "configuration object maps strategy"
//                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            IFieldName checkpointFieldName = new FieldName("checkpoint");
//                                                            IFieldName stepsFieldName = new FieldName("steps");
//                                                            IFieldName targetFieldName = new FieldName("target");
//
//                                                            if (a[0] instanceof List) {
//                                                                List<IObject> mapDescriptions = (List<IObject>) a[0];
//
//                                                                for (IObject mapDesc : mapDescriptions) {
//                                                                    Object cpDesc = mapDesc.getValue(checkpointFieldName);
//                                                                    List<IObject> stepsDec = (List<IObject>) mapDesc.getValue(stepsFieldName);
//
//                                                                    if (null != cpDesc) {
//                                                                        IObject cpStep = IOC.resolve(
//                                                                                IOC.resolve(
//                                                                                        IOC.getKeyForKeyStorage(),
//                                                                                        "checkpoint step"
//                                                                                ),
//                                                                                cpDesc
//                                                                        );
//
//                                                                        if (stepsDec.isEmpty() ||
//                                                                                !stepsDec.get(stepsDec.size() - 1).getValue(targetFieldName)
//                                                                                        .equals(cpStep.getValue(targetFieldName))) {
//                                                                            stepsDec.add(cpStep);
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            return a[0];
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(e);
//                                                        }
//                                                    }
//                                            )
//                                    );
//                                    IBiFunction findValueByArgument = (map, arg) -> {
//                                        char[] symbols = arg.toString().toCharArray();
//                                        String defaultKey = "default";
//                                        IResolveDependencyStrategy strategy = null;
//                                        StringBuilder key = new StringBuilder();
//                                        for (char c : symbols) {
//                                            key.append(c);
//                                            strategy = ((Map<String, IResolveDependencyStrategy>)map).get(key.toString());
//                                            if (null != strategy) {
//                                                break;
//                                            }
//                                        }
//                                        return null != strategy ? strategy : ((Map<String, IResolveDependencyStrategy>)map).get(defaultKey);
//                                    };
//                                    IResolveDependencyStrategy strategy = new StrategyStorageStrategy((a) -> a, findValueByArgument);
                                    IResolveDependencyStrategy strategy = new CObjectStrategy();
                                    ((IAdditionDependencyStrategy) strategy).register("in_", inStrategy);
                                    ((IAdditionDependencyStrategy) strategy).register("out_", outStrategy);
                                    ((IAdditionDependencyStrategy) strategy).register("exceptional", exceptionalStrategy);
                                    ((IAdditionDependencyStrategy) strategy).register("maps", mapsStrategy);
                                    ((IAdditionDependencyStrategy) strategy).register("default", defaultStrategy);
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "resolve key for configuration object"
                                            ),
//                                            new ApplyFunctionToArgumentsStrategy(
//                                                    (a) -> {
//                                                        try {
//                                                            Map<String, String> keys = new HashMap<String, String>() {{
//                                                                put("in_", "configuration object in_ strategy");
//                                                                put("out_", "configuration object out_ strategy");
//                                                                put("exceptional", "configuration object exceptional strategy");
//                                                                put("maps", "configuration object canonical maps strategy");
//                                                            }};
//                                                            char[] symbols = a[1].toString().toCharArray();
//                                                            String resolvedKey = "configuration object default strategy";
//                                                            StringBuilder key = new StringBuilder();
//                                                            for (char c : symbols) {
//                                                                key.append(c);
//                                                                if (null != keys.get(key.toString())) {
//                                                                    resolvedKey = keys.get(key.toString());
//                                                                    break;
//                                                                }
//                                                            }
//                                                            return IOC.resolve(
//                                                                    IOC.resolve(IOC.getKeyForKeyStorage(), resolvedKey),
//                                                                    a[0]
//                                                            );
//                                                        } catch (Throwable e) {
//                                                            throw new RuntimeException(
//                                                                    "Configuration object key resolution failed."
//                                                            );
//                                                        }
//                                                    }
//                                            )
                                            strategy
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "expandable_strategy#resolve key for configuration object"
                                            ),
                                            new SingletonStrategy(strategy)
                                    );
                                } catch (Exception e) {
                                    throw new ActionExecuteException(
                                            "Could not create or register some strategies for ConfigurationObject: " + e.getMessage(),
                                            e);
                                }
                            }
                        );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ConfigurationObject plugin': " + e.getMessage(), e);
        }
    }
}
