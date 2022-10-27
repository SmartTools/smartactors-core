package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.exception.ReceiverGeneratorException;

import java.lang.reflect.Method;

/**
 * Strategy that creates a invoker receiver.
 */
public class MethodInvokerReceiverStrategy implements IStrategy {
    private final IFieldName wrapperResolutionStrategyDependencyFieldName;
    private final IReceiverGenerator receiverGenerator;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies.
     */
    public MethodInvokerReceiverStrategy()
            throws ResolutionException {
        wrapperResolutionStrategyDependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "wrapperResolutionStrategyDependency");

        receiverGenerator = IOC.resolve(Keys.getKeyByName(IReceiverGenerator.class.getCanonicalName()));
    }

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        Object targetObject = args[0];
        Method method = (Method) args[1];
        IObject invokerConfig = (IObject) args[2];

        if (method.getParameterCount() != 1) {
            throw new StrategyException(
                    String.format("Unexpected amount of arguments in method %s of class %s.",
                            method.getName(), method.getDeclaringClass().getCanonicalName()));
        }

        try {
            Object wrapperResolutionStrategyDependency = invokerConfig.getValue(wrapperResolutionStrategyDependencyFieldName);

            if (null == wrapperResolutionStrategyDependency) {
                wrapperResolutionStrategyDependency = "default wrapper resolution strategy dependency for invoker receiver";
            }

            IStrategy wrapperResolutionStrategy = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), wrapperResolutionStrategyDependency),
                    method.getParameterTypes()[0]
            );

            return (T) receiverGenerator.generate(targetObject, wrapperResolutionStrategy, method.getName());
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | ReceiverGeneratorException e) {
            throw new StrategyException(e);
        }
    }
}
