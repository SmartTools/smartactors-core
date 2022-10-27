package info.smart_tools.smartactors.email_plugins.email_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.email.email_actor.MailingActor;
import info.smart_tools.smartactors.email.email_actor.exception.MailingActorException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.Arrays;

/**
 * Plugin for load strategy for MailingActor
 */
public class MailigActorPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public MailigActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("MailingActorPlugin");

            item
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IKey actorKey = Keys.getKeyByName(MailingActor.class.getCanonicalName());
                            IOC.register(actorKey,
                                    new ApplyFunctionToArgumentsStrategy(
                                            (args) -> {
                                                try {
                                                    return new MailingActor((IObject) args[0]);
                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                    throw new RuntimeException(
                                                            "Can't get args: args must contain one or more elements " +
                                                                    "and first element must be IObject",
                                                            e);
                                                } catch (MailingActorException e) {
                                                    throw new RuntimeException(
                                                            "Can't create actor with this args: "
                                                                    + Arrays.toString(args),
                                                            e);
                                                }
                                            }
                                    )
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("MailingActor plugin can't load: can't get MailingActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("MailingActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("MailingActor plugin can't load: can't register new strategy", e);
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem from one of reason", e);
        }
    }
}
