package info.smart_tools.smartactors.message_processing.handler_routing_receiver_creator;

import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.message_processing.handler_routing_receiver.HandlerRoutingReceiver;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IRoutedObjectCreator}.
 * Generates {@code HandlerReceivers} and puts its to the new instance of {@link HandlerRoutingReceiver}.
 */
public class HandlerRoutingReceiverCreator implements IRoutedObjectCreator {

    private FieldName name;
    private FieldName dependency;

    /**
     * Default constructor.
     * Create instance of {@link HandlerRoutingReceiverCreator} and initialize private fields.
     * @throws ObjectCreationException if {@link IOC} resolution failed.
     */
    public HandlerRoutingReceiverCreator()
            throws ObjectCreationException {
        try {
            this.name = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "name");
            this.dependency = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "dependency");
        } catch (Throwable e) {
            throw new ObjectCreationException("Could not create instance of HandlerRoutingReceiverCreator.");
        }
    }

    @Override
    public void createObject(final IRouter router, final IObject description)
            throws ObjectCreationException, InvalidArgumentException {

        try {
            Map<Object, IMessageReceiver> handlerReceiversMap = new HashMap<>();
            IWrapperGenerator wg = IOC.resolve(Keys.getOrAdd(IWrapperGenerator.class.getCanonicalName()));
            IReceiverGenerator rg = IOC.resolve(Keys.getOrAdd(IReceiverGenerator.class.getCanonicalName()));
            Object object = IOC.resolve(
                    Keys.getOrAdd((String) description.getValue(this.dependency)),
                    description
            );
            List<Method> methods = new LinkedList<>(Arrays.asList(object.getClass().getDeclaredMethods()));
            methods.removeIf(m -> m.isSynthetic() || !Modifier.isPublic(m.getModifiers()));
            for (Method m : methods) {
                Class wrapperInterface = m.getParameterTypes()[0];
                Object wrapper = wg.generate(wrapperInterface);
                IResolveDependencyStrategy strategy = new ApplyFunctionToArgumentsStrategy(
                        (arg) -> {
                            try {
                                return wrapper.getClass().newInstance();
                            } catch (Throwable e) {
                                throw new RuntimeException(
                                        "Could not create instance of " + wrapper.getClass().getCanonicalName() + "."
                                        , e
                                );
                            }
                        }
                );
                IMessageReceiver handlerReceiver = rg.generate(object, strategy, m.getName());
                handlerReceiversMap.put(m.getName(), handlerReceiver);
            }
            IMessageReceiver handlerRoutingReceiver = new HandlerRoutingReceiver(handlerReceiversMap);
            router.register(description.getValue(this.name), handlerRoutingReceiver);
        } catch (Throwable e) {
            throw new ObjectCreationException("Could not create receiver chain: " + e.getMessage(), e);
        }
    }
}
