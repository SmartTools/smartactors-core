package info.smart_tools.smartactors.core.ifield;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Support internal interface for wrappers
 */
public interface IField {

    /**
     * Apply rules and return result
     * @param <T> the return type for {@code in} method
     * @param env instance of {@link IObject} with data
     * @return instance of {@link T}
     * @throws ReadValueException if any errors occurred when iobject had been reading
     * @throws InvalidArgumentException if incoming arguments are incorrect
     * @throws ClassCastException when returning type doesn't match required type
     */
    <T> T in(final IObject env)
            throws ReadValueException, InvalidArgumentException;

    /**
     * Apply rules and return result
     * @param <T> the return type for {@code in} method
     * @param env instance of {@link IObject} with data
     * @param type the expected type of value
     * @return instance of {@link T}
     * @throws ReadValueException if any errors occurred when iobject had been reading
     * @throws InvalidArgumentException if incoming arguments are incorrect
     * @throws ClassCastException when returning type doesn't match required type
     */
    <T> T in(final IObject env, final Class type)
            throws ReadValueException, InvalidArgumentException;

    /**
     * Apply rules to given argument without return result
     * the type of inserting parameters for {@code out} method
     * @param <T> the type of incoming argument
     * @param env instance of {@link IObject} with data
     * @param in given argument
     * @param <T> Await out param
     * @throws ChangeValueException if any errors occurred when iObject had been changing
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    <T> void out(final IObject env, final T in)
            throws ChangeValueException, InvalidArgumentException;
}
