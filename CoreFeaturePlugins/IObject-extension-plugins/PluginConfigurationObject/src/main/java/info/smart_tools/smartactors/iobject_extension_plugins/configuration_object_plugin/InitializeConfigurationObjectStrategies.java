package info.smart_tools.smartactors.iobject_extension_plugins.configuration_object_plugin;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                                            throw new RuntimeException("Could not create new instance of Configuration Object.");
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "configuration object default strategy"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {
                                                        try {
                                                            return a[0];
                                                        } catch (Throwable e) {
                                                            throw new RuntimeException(
                                                                    "Error in configuration 'default' rule.", e
                                                            );
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "configuration object in_ strategy"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {
                                                        try {
                                                            Object obj = a[0];
                                                            if (obj instanceof String) {
                                                                IObject innerObject = new ConfigurationObject();
                                                                innerObject.setValue(new FieldName("name"), "wds_getter_strategy");
                                                                innerObject.setValue(new FieldName("args"), new ArrayList<String>() {{ add((String) obj); }});

                                                                return new ArrayList<IObject>() {{ add(innerObject); }};
                                                            }
                                                            return obj;
                                                        } catch (Throwable e) {
                                                            throw new RuntimeException(
                                                                    "Error in configuration 'wrapper' rule.", e
                                                            );
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "configuration object out_ strategy"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {
                                                        try {
                                                            Object obj = a[0];
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
                                                            throw new RuntimeException("Error in configuration 'wrapper' rule.", e);
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "configuration object exceptional strategy"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {
                                                        try {
                                                            Object obj = a[0];
                                                            if (obj instanceof List) {
                                                                for (IObject innerObject : (List<IObject>)obj) {
                                                                    if (null == innerObject.getValue(new FieldName("after"))) {
                                                                        innerObject.setValue(new FieldName("after"), "break");
                                                                    }
                                                                }
                                                            }
                                                            return obj;
                                                        } catch (Throwable e) {
                                                            throw new RuntimeException(
                                                                    "Error in configuration 'wrapper' rule.", e
                                                            );
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "checkpoint step"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    a -> {
                                                        try {
                                                            IObject checkpointConfig = (IObject) a[0];

                                                            IFieldName targetFieldName = new FieldName("target");
                                                            IFieldName handlerFieldName = new FieldName("handler");
                                                            IFieldName wrapperFieldName = new FieldName("wrapper");

                                                            checkpointConfig.setValue(targetFieldName, "checkpoint");
                                                            checkpointConfig.setValue(handlerFieldName, "enter");

                                                            checkpointConfig.setValue(wrapperFieldName, IOC.resolve(
                                                                    IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.getCanonicalName()),
                                                                    "{'in_getProcessor':'processor'}".replace('"', '\'')
                                                            ));

                                                            return checkpointConfig;
                                                        } catch (Throwable e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "configuration object maps strategy"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {
                                                        try {
                                                            IFieldName checkpointFieldName = new FieldName("checkpoint");
                                                            IFieldName stepsFieldName = new FieldName("steps");
                                                            IFieldName targetFieldName = new FieldName("target");

                                                            List<IObject> mapDescriptions = (List<IObject>) a[0];

                                                            for (IObject mapDesc : mapDescriptions) {
                                                                Object cpDesc = mapDesc.getValue(checkpointFieldName);
                                                                List<IObject> stepsDec = (List<IObject>) mapDesc.getValue(stepsFieldName);

                                                                if (null != cpDesc) {
                                                                    IObject cpStep = IOC.resolve(
                                                                            IOC.resolve(
                                                                                    IOC.getKeyForKeyStorage(),
                                                                                    "checkpoint step"
                                                                            ),
                                                                            cpDesc
                                                                    );

                                                                    if (stepsDec.isEmpty() ||
                                                                            !stepsDec.get(stepsDec.size() - 1).getValue(targetFieldName)
                                                                                    .equals(cpStep.getValue(targetFieldName))) {
                                                                        stepsDec.add(cpStep);
                                                                    }
                                                                }
                                                            }

                                                            return mapDescriptions;
                                                        } catch (Throwable e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            )
                                    );
                                    IOC.register(
                                            IOC.resolve(
                                                    IOC.getKeyForKeyStorage(), "resolve key for configuration object"
                                            ),
                                            new ApplyFunctionToArgumentsStrategy(
                                                    (a) -> {
                                                        try {
                                                            Map<String, String> keys = new HashMap<String, String>() {{
                                                                put("in_", "configuration object in_ strategy");
                                                                put("out_", "configuration object out_ strategy");
                                                                put("exceptional", "configuration object exceptional strategy");
                                                                put("maps", "configuration object maps strategy");
                                                            }};
                                                            char[] symbols = a[1].toString().toCharArray();
                                                            String resolvedKey = "configuration object default strategy";
                                                            StringBuilder key = new StringBuilder();
                                                            for (char c : symbols) {
                                                                key.append(c);
                                                                if (null != keys.get(key.toString())) {
                                                                    resolvedKey = keys.get(key.toString());
                                                                    break;
                                                                }
                                                            }
                                                            return IOC.resolve(
                                                                    IOC.resolve(IOC.getKeyForKeyStorage(), resolvedKey),
                                                                    a[0]
                                                            );
                                                        } catch (Throwable e) {
                                                            throw new RuntimeException(
                                                                    "Configuration object key resolution failed."
                                                            );
                                                        }
                                                    }
                                            )
                                    );
                                } catch (Exception e) {
                                    throw new ActionExecuteException(
                                            "Could not create or register some strategies for ConfigurationObject.",
                                            e);
                                }
                            }
                        );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'ConfigurationObject plugin'", e);
        }
    }
}
