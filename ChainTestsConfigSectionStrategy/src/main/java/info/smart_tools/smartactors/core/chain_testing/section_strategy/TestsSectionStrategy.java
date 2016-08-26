package info.smart_tools.smartactors.core.chain_testing.section_strategy;

import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.core.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.test.itest_runner.ITestRunner;
import info.smart_tools.smartactors.test.itest_runner.exception.TestExecutionException;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Strategy processing "tests" section of configuration.
 *
 * Requires thread pool initialized and task dispatcher running to execute tests.
 */
public class TestsSectionStrategy implements ISectionStrategy {
    private IFieldName name;
//    private ITestRunner runner;
    private IFieldName testRunnerName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public TestsSectionStrategy()
            throws ResolutionException {
        name = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "tests"
        );
        this.testRunnerName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "entryPoint"
        );
    }

    @Override
    public void onLoadConfig(final IObject config)
            throws ConfigurationProcessingException {
        try {
            List<IObject> tests = (List<IObject>) config.getValue(name);
            CyclicBarrier barrier = new CyclicBarrier(2);
            AtomicReference<Throwable> eRef = new AtomicReference<>(null);

            for (IObject testDesc : tests) {
                ITestRunner runner = IOC.resolve(
                        IOC.resolve(
                                IOC.getKeyForKeyStorage(),
                                ITestRunner.class.getCanonicalName() + "#" + testDesc.getValue(this.testRunnerName))
                );
                runner.runTest(testDesc, err -> {
                    eRef.set(err);
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (BrokenBarrierException e) {
                        throw new ActionExecuteException(e);
                    }
                });

                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                if (null != eRef.get()) {
                    throw new ConfigurationProcessingException("Test failed.", eRef.get());
                }
            }
        } catch (ReadValueException | ResolutionException | InvalidArgumentException | BrokenBarrierException | ClassCastException e) {
            throw new ConfigurationProcessingException(e);
        } catch (TestExecutionException e) {
            throw new ConfigurationProcessingException("Could not start test.", e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return name;
    }
}
