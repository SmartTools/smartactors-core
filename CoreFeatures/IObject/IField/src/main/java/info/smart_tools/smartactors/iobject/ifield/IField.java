package info.smart_tools.smartactors.iobject.ifield;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

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
    <T> T in(IObject env)
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
    <T> T in(IObject env, Class type)
            throws ReadValueException, InvalidArgumentException;

    /**
     * Apply rules to given argument without return result
     * the type of inserting parameters for {@code out} method
     * @param <T> the type of incoming argument
     * @param env instance of {@link IObject} with data
     * @param in given argument
     * @throws ChangeValueException if any errors occurred when iObject had been changing
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    <T> void out(IObject env, T in)
            throws ChangeValueException, InvalidArgumentException;
}
