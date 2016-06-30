package info.smart_tools.smartactors.core.iclass_generator;

import info.smart_tools.smartactors.core.iclass_generator.exception.ClassGenerationException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

/**
 * Interface IClassGenerator
 * @param <T> type of code source
 */
public interface IClassGenerator<T> {

    /**
     * Compile given source to java class
     * @param source source code for compilation
     * @return compiled class
     * @throws ClassGenerationException if any errors occurred
     * @throws InvalidArgumentException if arguments are incorrect
     */
    Class<?> generate(T source)
            throws ClassGenerationException, InvalidArgumentException;
}
