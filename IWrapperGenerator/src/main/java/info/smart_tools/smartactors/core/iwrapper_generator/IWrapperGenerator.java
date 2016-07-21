package info.smart_tools.smartactors.core.iwrapper_generator;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iwrapper_generator.exception.WrapperGeneratorException;

/**
 * IWrapperGenerator interface
 */
public interface IWrapperGenerator {

    /**
     * Generates wrapper class for {@link info.smart_tools.smartactors.core.iobject.IObject} that implements given interface.
     * @param targetInterface interface, which will be implemented
     * @param <T> type of given interface
     * @return instance of {@code Class<? extends T>} implementation of target interface
     * @throws InvalidArgumentException if target == null or if target is not an interface
     * @throws WrapperGeneratorException if class could not be generated
     */
    <T> T generate(Class<T> targetInterface)
            throws InvalidArgumentException, WrapperGeneratorException;
}
