package info.smart_tools.smartactors.testing_plugins.test_checkers_plugin;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.testing.interfaces.iresult_checker.IResultChecker;
import info.smart_tools.smartactors.testing.test_checkers.AssertionChecker;
import info.smart_tools.smartactors.testing.test_checkers.ExceptionInterceptor;

import java.util.List;

/**
 * Plugin registers strategies that returns new instances of {@link IResultChecker}.
 */
public class TestCheckersPlugin implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public TestCheckersPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("test checkers");
            item
//                    .after("IOC")
//                    .after("iobject")
//                    .after("IFieldNamePlugin")
                    .after("test assertions")
//                    .after("ConfigurationObject")
//                    .after("router")
                    .process(
                            () -> {
                                try {
                                    IOC.register(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IResultChecker.class.getCanonicalName() + "#assert"),
                                            new ApplyFunctionToArgumentsStrategy((args) -> {
                                                try {
                                                    return new AssertionChecker((List<IObject>) args[0]);
                                                } catch (InitializationException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            })
                                    );
                                    IOC.register(
                                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), IResultChecker.class.getCanonicalName() + "#intercept"),
                                            new ApplyFunctionToArgumentsStrategy((args) -> {
                                                try {
                                                    return new ExceptionInterceptor((IObject) args[0]);
                                                } catch (InitializationException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            })
                                    );
                                } catch (ResolutionException e) {
                                    throw new ActionExecutionException("TestCheckers plugin can't load: can't get ioc key.", e);
                                } catch (InvalidArgumentException e) {
                                    throw new ActionExecutionException("TestCheckers plugin can't load: can't create strategy.", e);
                                } catch (RegistrationException e) {
                                    throw new ActionExecutionException("TestCheckers plugin can't load: can't register new strategy.", e);
                                }
                            }
                        );
            this.bootstrap.add(item);
        } catch (Throwable e) {
            throw new PluginException("Could not load 'TestCheckers' plugin'", e);
        }
    }
}
