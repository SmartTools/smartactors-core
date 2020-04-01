package info.smart_tools.smartactors.checkpoint.scheduling_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import org.junit.Test;

import java.time.Duration;

import static org.mockito.Mockito.*;

/**
 * Test for {@link CheckpointRepeatStrategy}.
 */
public class CheckpointRepeatStrategyTest extends IOCInitializer {
    private CheckpointRepeatStrategy strategy;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        strategy = new CheckpointRepeatStrategy() {
            @Override
            protected long calculateNextInterval(ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException, ChangeValueException {
                return 0;
            }

            @Override
            protected Duration defaultPostRestoreDelay(ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException {
                return null;
            }

            @Override
            protected Duration defaultPostCompletionDelay(ISchedulerEntry entry) throws ReadValueException, InvalidArgumentException {
                return null;
            }
        };
    }

    @Test
    public void Should_awakePausedEntryWhenItIsUnpaused()
            throws Exception {
        ISchedulerEntry entry = mock(ISchedulerEntry.class);

        strategy.notifyPaused(entry);
        strategy.processPausedExecution(entry);

        verifyNoMoreInteractions(entry);

        strategy.notifyUnPaused(entry);

        verify(entry).awake();
    }
}

