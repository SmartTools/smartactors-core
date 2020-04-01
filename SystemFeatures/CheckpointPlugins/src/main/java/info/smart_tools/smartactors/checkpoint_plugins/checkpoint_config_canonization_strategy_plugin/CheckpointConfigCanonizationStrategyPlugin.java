package info.smart_tools.smartactors.checkpoint_plugins.checkpoint_config_canonization_strategy_plugin;

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

import java.util.List;
import java.util.Scanner;

public class CheckpointConfigCanonizationStrategyPlugin extends BootstrapPlugin {
    private String checkpointWrapperConfig;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CheckpointConfigCanonizationStrategyPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("checkpoint_config_canonization_strategies")
    public void registerCanonizationStrategies()
            throws ResolutionException, RegistrationException, InvalidArgumentException, StrategyRegistrationException {

        try (Scanner scanner = new Scanner(getClass().getResourceAsStream("checkpoint_wrapper_config.json"))) {
            checkpointWrapperConfig = scanner.useDelimiter("\\Z").next();
        }

        IOC.register(Keys.getKeyByName("checkpoint step configuration from checkpoint section"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                IObject checkpointConfig = (IObject) args[0];

                IFieldName targetFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "target");
                IFieldName handlerFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "handler");
                IFieldName wrapperFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "wrapper");

                checkpointConfig.setValue(targetFieldName, "checkpoint");
                checkpointConfig.setValue(handlerFieldName, "enter");

                checkpointConfig.setValue(wrapperFieldName, IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "configuration object"),
                        checkpointWrapperConfig
                ));

                return checkpointConfig;
            } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        }));

        IStrategyRegistration ads = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));

        ads.register("maps", new ApplyFunctionToArgumentsStrategy(args -> {
                try {
                    Object obj = args[1];

                    // TODO:: Remove the following copy-paste
                    // TODO:: Remove the following copy-paste
                    // TODO:: Remove the following copy-paste
                    if (obj instanceof List) {
                        for (IObject innerObject : (List<IObject>) obj) {
                            if (null == innerObject.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "externalAccess"))) {
                                innerObject.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "externalAccess"), false);
                            }
                            if (!innerObject.getValue(
                                    IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id"))
                                    .equals("tryToTakeResourceMap")) {
                                List exceptionalList = (List) innerObject.getValue(
                                        IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "exceptional"));

                                IObject outOfResourcesExceptionObj = IOC.resolve(Keys.getKeyByName("configuration object"));
                                outOfResourcesExceptionObj.setValue(
                                        IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "class"),
                                        "info.smart_tools.smartactors.base.interfaces.iresource_source.exceptions.OutOfResourceException");
                                outOfResourcesExceptionObj.setValue(
                                        IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain"),
                                        "tryToTakeResourceMap");
                                outOfResourcesExceptionObj.setValue(
                                        IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "after"),
                                        "break");
                                exceptionalList.add(0, outOfResourcesExceptionObj);
                            }
                        }
                    }
                    // Until here
                    // Until here
                    // Until here

                    if (obj instanceof List) {
                        for (IObject innerObject : (List<IObject>) obj) {
                            IFieldName checkpointFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpoint");
                            IFieldName stepsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "steps");

                            Object checkpointConf = innerObject.getValue(checkpointFieldName);
                            if (null != checkpointConf) {
                                Object checkpointStep = IOC.resolve(
                                        Keys.getKeyByName("checkpoint step configuration from checkpoint section"), checkpointConf);
                                ((List) innerObject.getValue(stepsFieldName)).add(checkpointStep);
                                innerObject.setValue(checkpointFieldName, null);
                            }
                        }
                    }

                    return obj;
                } catch (ResolutionException | InvalidArgumentException | ReadValueException | ChangeValueException e) {
                    throw new FunctionExecutionException(e);
                }
        }));
    }
}
