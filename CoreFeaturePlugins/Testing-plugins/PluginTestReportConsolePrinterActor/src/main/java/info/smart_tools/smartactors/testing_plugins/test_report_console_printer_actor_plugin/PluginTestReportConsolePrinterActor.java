package info.smart_tools.smartactors.testing_plugins.test_report_console_printer_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.testing.test_report_console_printer.TestReportConsolePrinterActor;

public class PluginTestReportConsolePrinterActor implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public PluginTestReportConsolePrinterActor(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("TestReportConsolePrinterActor");
            item.process(() -> {
                try {
                    IOC.register(Keys.getOrAdd("TestReportConsolePrinterActor"),
                            new ApplyFunctionToArgumentsStrategy(args -> {
                                try {
                                    return new TestReportConsolePrinterActor();
                                } catch (ResolutionException e) {
                                    throw new RuntimeException("Can't resolve TestReportConsolePrinterActor: " + e.getMessage(), e);
                                }
                            }));
                } catch (ResolutionException | InvalidArgumentException | RegistrationException e) {
                    throw new ActionExecuteException(e);
                }
            });
            this.bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
