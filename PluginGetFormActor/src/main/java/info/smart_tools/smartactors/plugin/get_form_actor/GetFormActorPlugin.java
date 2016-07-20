package info.smart_tools.smartactors.plugin.get_form_actor;

import info.smart_tools.smartactors.actors.get_form.GetFormActor;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Plugin for register actor for get form
 */
public class GetFormActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap the bootstrap
     */
    public GetFormActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("GetFormActorPlugin");
            item
                //.after("IOC")
                .process(() -> {
                    try {
                        IKey actorKey = Keys.getOrAdd(GetFormActor.class.toString());
                        IOC.register(actorKey, new CreateNewInstanceStrategy(
                                (args) -> {
                                    try {
                                        return new GetFormActor((IObject) args[0]);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
                    } catch (RegistrationException | InvalidArgumentException | ResolutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Can't load GetFormActor plugin", e);
        }
    }
}
