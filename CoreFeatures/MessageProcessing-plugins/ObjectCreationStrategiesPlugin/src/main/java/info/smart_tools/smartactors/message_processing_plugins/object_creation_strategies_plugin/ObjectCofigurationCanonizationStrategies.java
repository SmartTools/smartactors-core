package info.smart_tools.smartactors.message_processing_plugins.object_creation_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.exception.AdditionDependencyStrategyException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

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
    protected ObjectCofigurationCanonizationStrategies(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("object_configuration_canonization_strategies")
    @After({
            "basic_object_creators",
            "basic_object_kinds",
    })
    public void registerCanonizationStrategies()
            throws ResolutionException, RegistrationException, InvalidArgumentException, AdditionDependencyStrategyException {
        IOC.register(Keys.getOrAdd("canonize objects configuration section item filters list"),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    try {
                        IObject value = (IObject) args[1];

                        IFieldName filtersFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "filters");
                        IFieldName kindFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "kind");

                        List filtersList = (List) value.getValue(filtersFieldName);
                        String kindName = (String) value.getValue(kindFieldName);

                        if (null == filtersList) {
                            filtersList = new ArrayList();
                        }

                        if (null != kindName) {
                            filtersList.addAll(0, IOC.resolve(Keys.getOrAdd("object kind filter sequence#" + kindName)));
                        }

                        ListIterator iterator = filtersList.listIterator();

                        while (iterator.hasNext()) {
                            Object filterItem = iterator.next();

                            if (filterItem instanceof String) {
                                filterItem = IOC.resolve(Keys.getOrAdd("named filter config#" + filterItem));
                                iterator.set(filterItem);
                            }
                        }

                        value.setValue(filtersFieldName, filtersList);

                        return value;
                    } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException e) {
                        throw new FunctionExecutionException(e);
                    }
        }));

        IAdditionDependencyStrategy strategy = IOC.resolve(Keys.getOrAdd("expandable_strategy#resolve key for configuration object"));

        strategy.register("objects", new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                Object value = args[1];

                if (!(value instanceof List)) {
                    return value;
                }

                ListIterator<IObject> iterator = ((List<IObject>) value).listIterator();

                while (iterator.hasNext()) {
                    IObject objConf = iterator.next();

                    IObject updConf = IOC.resolve(Keys.getOrAdd("canonize objects configuration section item filters list"), objConf);

                    iterator.set(updConf);
                }

                return value;
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}
