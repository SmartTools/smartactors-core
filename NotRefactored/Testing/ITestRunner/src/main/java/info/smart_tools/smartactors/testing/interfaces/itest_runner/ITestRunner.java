package info.smart_tools.smartactors.testing.interfaces.itest_runner;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;

/**
 * Interface for realize different test runners.
 */
public interface ITestRunner {

    /**
     * Run a test described by the given {@link IObject}.
     *
     * @param description    description of the test to run
     * @param callback       callback to call when test is completed (with {@code null} as the only argument in case of success or with
     *                       exception describing failure reasons in case of failure)
     * @throws InvalidArgumentException if {@code description} is {@code null}
     * @throws InvalidArgumentException if {@code callback} is {@code null}
     * @throws TestExecutionException if test execution failed
     */
    void runTest(final IObject description, final IAction<Throwable> callback)
            throws InvalidArgumentException, TestExecutionException;
}
