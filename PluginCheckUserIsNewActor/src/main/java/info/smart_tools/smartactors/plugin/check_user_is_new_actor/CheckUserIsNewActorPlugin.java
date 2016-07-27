package info.smart_tools.smartactors.plugin.check_user_is_new_actor;


import info.smart_tools.smartactors.actors.check_user_is_new.CheckUserIsNewActor;
import info.smart_tools.smartactors.actors.check_user_is_new.wrapper.ActorParams;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.Arrays;

public class CheckUserIsNewActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public CheckUserIsNewActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CheckUserIsNewActorPlugin");

            item
                    .after("IOC")
                    .process(() -> {
                        try {
                            IKey checkUserIsNewActorKey = Keys.getOrAdd(CheckUserIsNewActor.class.getCanonicalName());
                            IOC.register(checkUserIsNewActorKey,
                                    new CreateNewInstanceStrategy(
                                            (args) -> {
                                                try {
                                                    ActorParams actorParams =
                                                            IOC.resolve(
                                                                    Keys.getOrAdd(ActorParams.class.getCanonicalName()),
                                                                    args[0]);
                                                    return new CheckUserIsNewActor(actorParams);
                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                    throw new RuntimeException(
                                                            "Can't get args: args must contain one or more elements " +
                                                                    "and first element must be IObject",
                                                            e);
                                                } catch (InvalidArgumentException e) {
                                                    throw new RuntimeException(
                                                            "Can't create actor with this args: "
                                                                    + Arrays.toString(args),
                                                            e);
                                                } catch (ResolutionException e) {
                                                    throw new RuntimeException(
                                                            "Can't get ActorParams wrapper or Key for ActorParams",
                                                            e);
                                                }
                                            }
                                    )
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("Can't get CheckUserIsNewActor key");
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("Can't get create strategy");
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("Can't get register new strategy");
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem from one of reason", e);
        }
    }
}
