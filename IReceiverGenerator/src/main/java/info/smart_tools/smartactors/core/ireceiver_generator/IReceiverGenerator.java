package info.smart_tools.smartactors.core.ireceiver_generator;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ireceiver_generator.exception.ReceiverGeneratorException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;

/**
 * IReceiverGenerator interface
 */
public interface IReceiverGenerator {

    /**
     * Generates instance of {@link IMessageReceiver}.
     * @param instance the instance of users class
     * @param wrapperResolutionStrategy the strategy for resolve wrapper
     * @param methodName the name of method
     * @return the new instance of {@link IMessageReceiver}
     * @throws InvalidArgumentException if {@code params} responds inherent requirements
     * @throws ReceiverGeneratorException if class could not be generated
     */
    IMessageReceiver generate(
            Object instance,
            IResolveDependencyStrategy wrapperResolutionStrategy,
            String methodName
    )
            throws InvalidArgumentException, ReceiverGeneratorException;
}
