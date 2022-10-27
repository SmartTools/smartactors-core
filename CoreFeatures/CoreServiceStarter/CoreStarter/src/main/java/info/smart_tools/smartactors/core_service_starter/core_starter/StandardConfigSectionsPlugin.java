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
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_extension.configuration_object.ConfigurationObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.ArrayList;
import java.util.List;

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
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            configurationManager.addSectionStrategy(new ObjectsSectionProcessingStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            ISectionStrategy sectionStrategy = new ObjectsSectionProcessingStrategy();
                            configurationManager.removeSectionStrategy(sectionStrategy.getSectionName());
                        } catch (InvalidArgumentException e) {
                            System.out.println("[WARNING] Deregistration of \"ObjectsSectionProcessingStrategy\" has failed while reverting \"config_section:objects\" plugin.");
                        } catch (ResolutionException e) { }
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
                                                    if (null == innerObject.getValue(new FieldName("externalAccess"))) {
                                                        innerObject.setValue(new FieldName("externalAccess"), false);
                                                    }
                                                    if (!innerObject.getValue(new FieldName("id")).equals("tryToTakeResourceMap")) {
                                                        List exceptionalList = (List) innerObject.getValue(new FieldName("exceptional"));

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
                                            throw new RuntimeException("Error in configuration 'canonical maps' rule.", e);
                                        }
                                    })
                            );
                            strategy.register("in_", new ApplyFunctionToArgumentsStrategy(
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
                                    })
                            );
                            strategy.register("exceptional",  new ApplyFunctionToArgumentsStrategy(
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

        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
