package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.exception.ReceiverGeneratorException;

import java.lang.reflect.Method;

/**
 * Strategy that creates a invoker receiver.
 */
public class MethodInvokerReceiverResolutionStrategy implements IResolveDependencyStrategy {
    private final IFieldName wrapperResolutionStrategyDependencyFieldName;
    private final IReceiverGenerator receiverGenerator;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies.
     */
    public MethodInvokerReceiverResolutionStrategy()
            throws ResolutionException {
        wrapperResolutionStrategyDependencyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                "wrapperResolutionStrategyDependency");

        receiverGenerator = IOC.resolve(Keys.getOrAdd(IReceiverGenerator.class.getCanonicalName()));
    }

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        Object targetObject = args[0];
        Method method = (Method) args[1];
        IObject invokerConfig = (IObject) args[2];

        if (method.getParameterCount() != 1) {
            throw new ResolveDependencyStrategyException(
                    String.format("Unexpected amount of arguments in method %s of class %s.",
                            method.getName(), method.getDeclaringClass().getCanonicalName()));
        }

        try {
            Object wrapperResolutionStrategyDependency = invokerConfig.getValue(wrapperResolutionStrategyDependencyFieldName);

            if (null == wrapperResolutionStrategyDependency) {
                wrapperResolutionStrategyDependency = "default wrapper resolution strategy dependency for invoker receiver";
            }

            IResolveDependencyStrategy wrapperResolutionStrategy = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyStorage(), wrapperResolutionStrategyDependency),
                    method.getParameterTypes()[0]
            );

            return (T) receiverGenerator.generate(targetObject, wrapperResolutionStrategy, method.getName());
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | ReceiverGeneratorException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
