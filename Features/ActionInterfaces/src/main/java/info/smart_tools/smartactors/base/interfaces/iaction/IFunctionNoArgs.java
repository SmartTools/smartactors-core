package info.smart_tools.smartactors.base.interfaces.iaction;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;

/**
 * Interface for function with 0 arguments
 *
 * @param <R> the function result type
 */
public interface IFunctionNoArgs<R> {
    /**
     * Execute the function.
     *
     * @return result of the function
     * @throws FunctionExecutionException if any error occurs
     */
    R execute() throws FunctionExecutionException;
}
