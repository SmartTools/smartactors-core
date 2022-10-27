package info.smart_tools.smartactors.shutdown_plugins.root_up_counter_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.IllegalUpCounterState;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.up_counter.UpCounter;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

public class RootUpCounterPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public RootUpCounterPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    /**
     * Creates and registers root upcounter.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering the upcounter
     * @throws InvalidArgumentException if {@link SingletonStrategy} does not accept the upcounter instance
     */
    @Item("root_upcounter")
    public void registerRootUpcounter()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("root upcounter"), new SingletonStrategy(new UpCounter()));
    }

    /**
     * Reverts root upcounter registrations.
     *
     */
    @ItemRevert("root_upcounter")
    public void unregisterRootUpcounter() {
        String[] itemNames = { "root upcounter" };
        Keys.unregisterByNames(itemNames);
    }

    /**
     * Registers strategy for creation of new upcounters. Strategy takes one optional argument - the parent upcounter. By default the root
     * upcounter is used as parent.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering the upcounter
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} doesn't accept a function
     */
    @Item("new_upcounter_creation_strategy")
    @After({
        "root_upcounter",
    })
    public void registerNewUpcounterCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("new upcounter"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                IUpCounter parent = args.length > 0 ? (IUpCounter) args[0] : IOC.resolve(Keys.getKeyByName("root upcounter"));

                return new UpCounter(parent);
            } catch (ResolutionException | IllegalUpCounterState e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Reverts strategy for creation of new upcounters registration.
     *
     */
    @ItemRevert("new_upcounter_creation_strategy")
    public void unregisterNewUpcounterCreationStrategy() {
        String[] itemNames = { "new upcounter" };
        Keys.unregisterByNames(itemNames);
    }

    /**
     * Barrier item. All upcounters for core systems should be registered between "core_upcounters_setup:start" and
     * "core_upcounters_setup:done".
     */
    @Item("core_upcounters_setup:start")
    @After({
        "root_upcounter",
        "new_upcounter_creation_strategy",
    })
    public void upcountersBarrierStart() {
    }

    /**
     * Barrier item. All upcounters for core systems should be registered between "core_upcounters_setup:start" and
     * "core_upcounters_setup:done".
     */
    @Item("core_upcounters_setup:done")
    @After({
        "core_upcounters_setup:start"
    })
    public void upcouterBarrierEnd() {
    }

    private static final long TERMINATION_TIMEOUT = 30000;

    /**
     * Register callbacks on shutdown completion at root upcounter.
     *
     * @throws ResolutionException if error occurs resolving root upcounter
     * @throws UpCounterCallbackExecutionException if system is already down and error occurs executing some callbacks
     */
    @Item("default_shutdown_callbacks")
    @After({
        "root_upcounter"
    })
    @Before({"read_initial_config"})
    public void setupDefaultShutdownCallbacks()
            throws ResolutionException, UpCounterCallbackExecutionException {
        IUpCounter upCounter = IOC.resolve(Keys.getKeyByName("root upcounter"));

        upCounter.onShutdownRequest(this.toString(), mode -> {
            System.out.printf("Got shutdown request with mode=\"%s\"\n", mode);
        });

        upCounter.onShutdownComplete(this.toString(), () -> {
            System.out.println("Shutting down completely...");
        });

        // TODO:: Remove/move to separate feature if multiple instances will be able to run in the same JVM
        upCounter.onShutdownComplete(this.toString(), () -> {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(TERMINATION_TIMEOUT / 2);
                    System.out.println("Or not...");
                    Thread.sleep(TERMINATION_TIMEOUT / 2);
                    System.out.println("Forcing JVM shutdown...");
                    System.exit(1);
                } catch (InterruptedException ignore) { }
            }, "onShutdownCompleteThread");

            thread.setDaemon(true);
            thread.start();
        });
    }
}
