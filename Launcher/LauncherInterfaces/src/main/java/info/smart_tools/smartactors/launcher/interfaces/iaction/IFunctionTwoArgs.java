package info.smart_tools.smartactors.launcher.interfaces.iaction;

import info.smart_tools.smartactors.launcher.interfaces.exception.iaction.FunctionExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iaction.InvalidArgumentException;

/**
 * Interface IFunctionTwoArgs
 *
 * @param <T1> the type of the first argument of the function
 * @param <T2> the type of the second argument of the function
 * @param <R>  the type of the result of the function
 */
public interface IFunctionTwoArgs<T1, T2, R> {

    /**
     * Executes this function to the given arguments.
     *
     * @param t1 the first function argument
     * @param t2 the second function argument
     * @return the function result
     * @throws FunctionExecutionException if any errors occurred
     * @throws InvalidArgumentException   if incoming argument are incorrect
     */
    R execute(T1 t1, T2 t2)
            throws FunctionExecutionException, InvalidArgumentException;
}
