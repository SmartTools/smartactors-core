package info.smart_tools.smartactors.http_endpoint_plugins.plugins_deferred_response_actor;

import info.smart_tools.deferred_response.DeferredResponse;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.http_endpoint.deferred_response_actor.DeferredResponseActor;
import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.IDeferredResponse;
import info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.exception.DeferredResponseException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin for register {@link DeferredResponseActor} in IOC.
 */
public class PluginDeferredResponseActor extends BootstrapPlugin {

    public PluginDeferredResponseActor(IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("plugin-deferred-response-actor")
    public void registerDeferredResponseActor() throws ResolutionException, InvalidArgumentException, RegistrationException {
        IOC.register(
                Keys.getOrAdd(DeferredResponseActor.class.getSimpleName()),
                new ApplyFunctionToArgumentsStrategy(
                        args -> new DeferredResponseActor()
                ));
    }

    @Item("plugin-deferred-response")
    public void registerDeferredResponse() throws ResolutionException, InvalidArgumentException, RegistrationException {
        IFieldName contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        IFieldName sendResponseOnChainEndFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "sendResponseOnChainEnd");

        IOC.register(
                Keys.getOrAdd(IDeferredResponse.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        args -> {
                            try {
                                IObjectWrapper wrapper = (IObjectWrapper) args[0];
                                IObject context = wrapper.getEnvironmentIObject(contextFieldName);
                                context.setValue(sendResponseOnChainEndFieldName, true);
                                return new DeferredResponse(wrapper);
                            } catch (DeferredResponseException | ChangeValueException e) {
                                throw new FunctionExecutionException("Can't create deferrd response", e);
                            }
                        }
                )
        );
    }

}
