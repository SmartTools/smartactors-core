package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class StandardConfigSectionsPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public StandardConfigSectionsPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            /* "objects" section */
            IBootstrapItem<String> objectsSectionItem = new BootstrapItem("config_section:objects");
            objectsSectionItem
                    .after("config_sections:start")
                    .before("config_sections:done")
                    .after("router")
                    .after("IFieldNamePlugin")
                    .before("starter")
                    .after("object_creation_strategies:done")
                    .process(() -> {
                        try(InputStream stream = getClass().getClassLoader().getResourceAsStream("objects_section_schema.json")) {
                            if (stream == null) {
                                throw new ActionExecutionException("Schema for section \"objects\" not found in resources.");
                            }

                            IFieldName sectionNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");
                            String schema = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));

                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            configurationManager.addSectionStrategy(new ObjectsSectionProcessingStrategy(
                                    sectionNameFieldName,
                                    schema
                            ));
                        } catch (ResolutionException | InvalidArgumentException | IOException e) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IFieldName sectionNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");

                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            configurationManager.removeSectionStrategy(sectionNameFieldName);
                        } catch (InvalidArgumentException e) {
                            System.out.println("[WARNING] Deregistration of \"ObjectsSectionProcessingStrategy\" has failed while reverting \"config_section:objects\" plugin.");
                        } catch (ResolutionException e) {
                            // ToDo: !!! Unhandled exception
                        }
                    });

            bootstrap.add(objectsSectionItem);

            /* "maps" section */
            IBootstrapItem<String> mapsSectionItem = new BootstrapItem("config_section:maps");
            mapsSectionItem
                    .after("config_sections:start")
                    .before("config_sections:done")
                    .after("config_section:objects")
                    .after("receiver_chains_storage")
                    .after("receiver_chain")
                    .after("IFieldNamePlugin")
                    .after("ConfigurationObject")
                    .before("starter")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new MapsSectionProcessingStrategy());
                            IStrategyRegistration strategy = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));

                            strategy.register("maps", new ApplyFunctionToArgumentsStrategy(
                                    (a) -> {
                                        try {
                                            Object obj = a[1];

                                            if (obj instanceof List) {
                                                for (IObject innerObject : (List<IObject>) obj) {
                                                    if (null == innerObject.getValue(
                                                            IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "externalAccess"))
                                                    ) {
                                                        innerObject.setValue(
                                                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "externalAccess"),
                                                                false
                                                        );
                                                    }
                                                    if (!innerObject.getValue(
                                                            IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "id")
                                                    ).equals("tryToTakeResourceMap")) {
                                                        List exceptionalList = (List) innerObject.getValue(
                                                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "exceptional")
                                                        );

                                                        IObject outOfResourcesExceptionObj = IOC.resolve(Keys.getKeyByName("configuration object"));
                                                        outOfResourcesExceptionObj.setValue(
                                                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "class"),
                                                                "info.smart_tools.smartactors.base.interfaces.iresource_source.exceptions.OutOfResourceException"
                                                        );
                                                        outOfResourcesExceptionObj.setValue(
                                                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "chain"),
                                                                "tryToTakeResourceMap"
                                                        );
                                                        outOfResourcesExceptionObj.setValue(
                                                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "after"),
                                                                "break"
                                                        );
                                                        exceptionalList.add(0, outOfResourcesExceptionObj);
                                                    }
                                                }
                                            }
                                            return obj;
                                        } catch (Throwable e) {
                                            throw new RuntimeException("Error in configuration 'canonical maps' rule.", e);
                                        }
                                    })
                            );
                            strategy.register("in_", new ApplyFunctionToArgumentsStrategy(
                                    (a) -> {
                                        try {
                                            Object obj = a[1];
                                            if (obj instanceof String) {
                                                IObject innerObject = IOC.resolve(Keys.getKeyByName("configuration object"));
                                                innerObject.setValue(
                                                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "name"),
                                                        "wds_getter_strategy"
                                                );
                                                innerObject.setValue(
                                                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "args"),
                                                        new ArrayList<String>() {{ add((String) obj); }}
                                                );

                                                return new ArrayList<IObject>() {{ add(innerObject); }};
                                            }
                                            return obj;
                                        } catch (Throwable e) {
                                            throw new RuntimeException(
                                                    "Error in configuration 'wrapper' rule.", e
                                            );
                                        }
                                    })
                            );
                            strategy.register("out_", new ApplyFunctionToArgumentsStrategy(
                                    (a) -> {
                                        try {
                                            Object obj = a[1];
                                            if (obj instanceof String) {
                                                IObject innerObject = IOC.resolve(Keys.getKeyByName("configuration object"));
                                                innerObject.setValue(
                                                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "name"),
                                                        "wds_target_strategy"
                                                );
                                                innerObject.setValue(
                                                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "args"),
                                                        new ArrayList<String>() {{ add("local/value"); add((String) obj); }}
                                                );

                                                return new ArrayList<List<IObject>>() {{
                                                    add(new ArrayList<IObject>() {{  add(innerObject); }});
                                                }};
                                            }
                                            if (obj instanceof List) {
                                                for (Object o : (List) obj) {
                                                    if (o instanceof List) {
                                                        for (Object innerObject : (List) o) {
                                                            if (((IObject) innerObject).getValue(
                                                                    IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "name")
                                                            ).equals("target")) {
                                                                ((IObject) innerObject).setValue(
                                                                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "name"),
                                                                        "wds_target_strategy"
                                                                );
                                                                ((IObject) innerObject).setValue(
                                                                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "args"), new ArrayList<String>() {{
                                                                            add("local/value");
                                                                            add((String) ((List) ((IObject) innerObject)
                                                                                    .getValue(IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "args"))).get(0));
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
                                    })
                            );
                            strategy.register("exceptional",  new ApplyFunctionToArgumentsStrategy(
                                    (a) -> {
                                        try {
                                            Object obj = a[1];
                                            if (obj instanceof List) {
                                                for (IObject innerObject : (List<IObject>) obj) {
                                                    if (
                                                            null == innerObject.getValue(IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "after"))
                                                    ) {
                                                        innerObject.setValue(
                                                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "after"),
                                                                "break"
                                                        );
                                                    }
                                                }
                                            }
                                            return obj;
                                        } catch (Throwable e) {
                                            throw new RuntimeException(
                                                    "Error in configuration 'exceptional' rule.", e
                                            );
                                        }
                                    })
                            );
                        } catch (ResolutionException | InvalidArgumentException | StrategyRegistrationException e) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IStrategyRegistration strategy = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));
                            try {
                                strategy.unregister("exceptional");
                            } catch (StrategyRegistrationException e) {
                                System.out.println("[WARNING] Deregistration of \"exceptional\" strategy has failed while reverting \"config_section:maps\" plugin.");
                            }
                            try {
                                strategy.unregister("out_");
                            } catch (StrategyRegistrationException e) {
                                System.out.println("[WARNING] Deregistration of \"out_\" strategy has failed while reverting \"config_section:maps\" plugin.");
                            }
                            try {
                                strategy.unregister("in_");
                            } catch (StrategyRegistrationException e) {
                                System.out.println("[WARNING] Deregistration of \"in_\" strategy has failed while reverting \"config_section:maps\" plugin.");
                            }
                            try {
                                strategy.unregister("maps");
                            } catch (StrategyRegistrationException e) {
                                System.out.println("[WARNING] Deregistration of \"maps\" strategy has failed while reverting \"config_section:maps\" plugin.");
                            }
                        } catch (ResolutionException e) { }
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            ISectionStrategy sectionStrategy = new MapsSectionProcessingStrategy();
                            configurationManager.removeSectionStrategy(sectionStrategy.getSectionName());
                        } catch (InvalidArgumentException e) {
                            System.out.println("[WARNING] Deregistration of \"MapsSectionProcessingStrategy\" has failed while reverting \"config_section:maps\" plugin.");
                        } catch (ResolutionException e) { }
                    });

            bootstrap.add(mapsSectionItem);

            /* "executor" section */
            IBootstrapItem<String> executorSectionItem = new BootstrapItem("config_section:executor");

            executorSectionItem
                    .after("config_sections:start")
                    .before("config_sections:done")
                    .after("queue")
                    .after("IFieldNamePlugin")
                    .after("root_upcounter")
                    .before("starter")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new ExecutorSectionProcessingStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            ISectionStrategy sectionStrategy = new ExecutorSectionProcessingStrategy();
                            configurationManager.removeSectionStrategy(sectionStrategy.getSectionName());
                        } catch (InvalidArgumentException e) {
                            System.out.println("[WARNING] Deregistration of \"ExecutorSectionProcessingStrategy\" has failed while reverting \"config_section:executor\" plugin.");
                        } catch (ResolutionException e) { }
                    });

            bootstrap.add(executorSectionItem);

        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}
