package info.smart_tools.smartactors.testing.chain_testing.section_strategy;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.testing.interfaces.itest_reporter.ITestReporter;
import info.smart_tools.smartactors.testing.interfaces.itest_reporter.exception.TestReporterException;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Strategy processing "tests" section of configuration.
 * <p>
 * Requires thread pool initialized and task dispatcher running to execute tests.
 */
public class TestsSectionStrategy implements ISectionStrategy {
    private final IField testCasesSectionField;
    private final IFieldName testRunnerName;
    private final IFieldName featureNameField;
    private final IFieldName testSectionField;
    private final IFieldName testNameFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public TestsSectionStrategy()
            throws ResolutionException {
        this.testCasesSectionField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "tests"
        );
        this.testSectionField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "test"
        );
        this.testRunnerName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "entryPoint"
        );
        this.featureNameField = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "featureName");
        this.testNameFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()), "name");
    }

    @Override
    public void onLoadConfig(final IObject config)
            throws ConfigurationProcessingException {
        try {
            System.out.println("--------------------------------- Run testing ---------------------------------");

            final IObject testConfig = (IObject) config.getValue(testSectionField);
            List<IObject> tests;
            if (testConfig == null) {
                // Test section isn't described in config.json.
                // Skip test
                tests = new ArrayList<>();
            } else {
                tests = testCasesSectionField.in(testConfig);
            }
            final CyclicBarrier barrier = new CyclicBarrier(2);
            final AtomicReference<Throwable> eRef = new AtomicReference<>(null);
            final ITestReporter testReporter = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyStorage(), ITestReporter.class.getCanonicalName()));

            for (final IObject testDesc : tests) {
                System.out.println("Run test '" + testDesc.getValue(testNameFieldName) + "'.");
                testReporter.beforeTest(testDesc);
                final ITestRunner runner = IOC.resolve(
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
                testReporter.afterTest(eRef.get());

                if (null != eRef.get()) {
                    throw new ConfigurationProcessingException("Test '"+testDesc.getValue(testNameFieldName)+"' failed ", eRef.get());
                } else {
                    System.out.println("Test '" + testDesc.getValue(testNameFieldName) + "' is successful.");
                }
            }
            System.out.println("--------------------------------- Testing completed ---------------------------------");
            testReporter.make((String) config.getValue(featureNameField), testConfig);
        } catch (ReadValueException | ResolutionException | InvalidArgumentException | BrokenBarrierException | ClassCastException e) {
            throw new ConfigurationProcessingException(e);
        } catch (TestExecutionException e) {
            throw new ConfigurationProcessingException("Could not start test.", e);
        } catch (TestReporterException e) {
            throw new ConfigurationProcessingException("Could not make test report.", e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return testSectionField;
    }
}
