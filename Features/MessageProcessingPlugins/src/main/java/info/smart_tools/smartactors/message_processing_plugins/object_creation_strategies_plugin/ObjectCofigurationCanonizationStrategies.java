package info.smart_tools.smartactors.message_processing_plugins.object_creation_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Register canonization strategies for "objects" section that replace old-style "kind" field with additional filters in filters list.
 */
public class ObjectCofigurationCanonizationStrategies extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public ObjectCofigurationCanonizationStrategies(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("object_configuration_canonization_strategies")
    @After({
            "basic_object_creators",
            "basic_object_kinds",
            "ConfigurationObject",
    })
    public void registerCanonizationStrategies()
            throws ResolutionException, RegistrationException, InvalidArgumentException, StrategyRegistrationException {
        IOC.register(Keys.getKeyByName("canonize objects configuration section item filters list"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        IObject value = (IObject) args[0];

                        IFieldName filtersFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "filters");
                        IFieldName kindFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "kind");

                        List filtersList = (List) value.getValue(filtersFieldName);
                        String kindName = (String) value.getValue(kindFieldName);

                        if (null == filtersList) {
                            filtersList = new ArrayList();
                        } else {
                            filtersList = new ArrayList(filtersList);
                        }

                        if (null != kindName) {
                            filtersList.addAll(0, IOC.resolve(Keys.getKeyByName("object kind filter sequence#" + kindName)));
                        }

                        ListIterator iterator = filtersList.listIterator();

                        while (iterator.hasNext()) {
                            Object filterItem = iterator.next();

                            if (filterItem instanceof String) {
                                filterItem = IOC.resolve(Keys.getKeyByName("named filter config#" + filterItem));
                                iterator.set(filterItem);
                            }
                        }

                        value.setValue(filtersFieldName, filtersList);

                        return value;
                    } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException e) {
                        throw new FunctionExecutionException(e);
                    }
        }));

        IStrategyRegistration strategy = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));

        strategy.register("objects", new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                Object value = args[1];

                if (!(value instanceof List)) {
                    return value;
                }

                ListIterator<IObject> iterator = ((List<IObject>) value).listIterator();

                while (iterator.hasNext()) {
                    IObject objConf = iterator.next();

                    IObject updConf = IOC.resolve(Keys.getKeyByName("canonize objects configuration section item filters list"), objConf);

                    iterator.set(updConf);
                }

                return value;
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    @ItemRevert("object_configuration_canonization_strategies")
    public void unregisterCanonizationStrategies() {
        String[] keyNames = { "canonize objects configuration section item filters list" };
        Keys.unregisterByNames(keyNames);
    }
}
