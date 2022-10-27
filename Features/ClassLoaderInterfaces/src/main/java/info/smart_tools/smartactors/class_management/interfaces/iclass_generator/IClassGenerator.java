package info.smart_tools.smartactors.class_management.interfaces.iclass_generator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.iclass_generator.exception.ClassGenerationException;

/**
 * Interface IClassGenerator
 * @param <T> type of code source
 */
public interface IClassGenerator<T> {

    /**
     * Compile given source to java class
     * @param source source code for compilation
     * @param classLoader class loader to store generated class
     * @return compiled class
     * @throws ClassGenerationException if any errors occurred
     * @throws InvalidArgumentException if arguments are incorrect
     */
    Class<?> generate(T source, ClassLoader classLoader)
            throws ClassGenerationException, InvalidArgumentException;
}
