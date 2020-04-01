package info.smart_tools.smartactors.scheduler.strategies;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ContinuouslyRepeatScheduleStrategy}.
 */
public class ContinuouslyRepeatScheduleStrategyTest extends IOCInitializer {
    private ISchedulerEntry entry;
    private IFieldName start;
    private IFieldName interval;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        entry = mock(ISchedulerEntry.class);
        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));

        start = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "start");
        interval = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "interval");
    }

    @Test
    public void Should_cancelEntryWhenExceptionOccurs()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();

        strategy.processException(entry, mock(Throwable.class));

        verify(entry).cancel();
    }

    @Test(expected = SchedulingStrategyExecutionException.class)
    public void Should_wrapExceptionWhenEntryCancellationThrows()
            throws Exception {
        doThrow(EntryScheduleException.class).when(entry).cancel();

        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();

        strategy.processException(entry, mock(Throwable.class));
    }

    @Test
    public void Should_initializeEntry()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();

        strategy.init(entry,
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                    "{'start':'3410-01-01T00:00:01','interval':'PT24H'}".replace('\'','"')));

        // Happy new year to the people of 3410'th year (if you are alive) (if you still use smartactors)
        assertEquals("3410-01-01T00:00:01", entry.getState().getValue(start));
        assertEquals("PT24H", entry.getState().getValue(interval));

        verify(entry).scheduleNext(LocalDateTime.parse("3410-01-01T00:00:01").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());
        verify(entry).save();
    }

    @Test
    public void Should_notSaveEntryIfSaveFlagIsSetToFalse()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();

        strategy.init(entry,
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                    "{'start':'3410-01-01T00:00:01','interval':'PT24H','save':false}".replace('\'','"')));

        verify(entry).scheduleNext(LocalDateTime.parse("3410-01-01T00:00:01").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());
        verify(entry, times(0)).save();
    }

    @Test
    public void Should_initializeEntryUsingCurrentTimeIfNoTimeGiven()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();

        strategy.init(entry,
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                    "{'interval':'PT24H'}".replace('\'','"')));

        assertTrue(Duration.between(
                LocalDateTime.parse((String) entry.getState().getValue(start)),
                LocalDateTime.now(ZoneOffset.UTC)).abs().getSeconds()
                    <= 1);
        assertEquals("PT24H", entry.getState().getValue(interval));
    }

    @Test
    public void Should_restoreAndPostProcessEntry()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();
        when(entry.getState()).thenReturn(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                        "{'start':'1994-10-15T17:32:05','interval':'PT72H'}".replace('\'','"')));

        strategy.restore(entry);

        assertEquals("1994-10-15T17:32:05", entry.getState().getValue(start));
        assertEquals("PT72H", entry.getState().getValue(interval));
        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(long.class);
        verify(entry).scheduleNext(timeCaptor.capture());
        long captured0 = timeCaptor.getValue();
        assertEquals(0,
                (captured0 - LocalDateTime.parse("1994-10-15T17:32:05").atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                % Duration.parse("PT72H").toMillis());
        assertTrue(System.currentTimeMillis() < captured0);

        when(entry.getLastTime()).thenReturn(captured0);

        strategy.postProcess(entry);

        verify(entry, times(2)).scheduleNext(timeCaptor.capture());
        assertEquals(Duration.parse("PT72H").toMillis(), timeCaptor.getValue() - captured0);
    }

    @Test
    public void Should_initializeEntryWithPeriodInterval()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();

        strategy.init(entry,
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                        "{'start':'3410-01-01T00:00:01','interval':'P2M'}".replace('\'','"')));

        // Happy new year to the people of 3410'th year (if you are alive) (if you still use smartactors)
        assertEquals("3410-01-01T00:00:01", entry.getState().getValue(start));
        assertEquals("P2M", entry.getState().getValue(interval));

        verify(entry).scheduleNext(LocalDateTime.parse("3410-01-01T00:00:01").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());
    }

    @Test
    public void Should_restoreAndPostProcessEntryWithPeriodInterval()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();
        when(entry.getState()).thenReturn(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                        "{'start':'1994-10-15T17:32:05','interval':'P2M'}".replace('\'','"')));

        strategy.restore(entry);

        assertEquals("1994-10-15T17:32:05", entry.getState().getValue(start));
        assertEquals("P2M", entry.getState().getValue(interval));
        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(long.class);
        verify(entry).scheduleNext(timeCaptor.capture());
        long captured0 = timeCaptor.getValue();

        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(captured0), ZoneOffset.UTC).getDayOfMonth(), 15);

        when(entry.getLastTime()).thenReturn(captured0);

        strategy.postProcess(entry);

        verify(entry, times(2)).scheduleNext(timeCaptor.capture());
        long nextInterval = LocalDateTime.ofInstant(Instant.ofEpochMilli(captured0), ZoneOffset.UTC).plus(Period.ofMonths(2)).atZone(ZoneOffset.UTC).toInstant().toEpochMilli() - captured0;
        assertEquals(nextInterval, timeCaptor.getValue() - captured0);
    }

    @Test
    public void Should_reschedulePausedEntry()
            throws Exception {
        ISchedulingStrategy strategy = new ContinuouslyRepeatScheduleStrategy();
        when(entry.getState()).thenReturn(
                IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                        "{'start':'1994-10-15T17:32:05','interval':'P2M'}".replace('\'','"')));

        strategy.notifyPaused(entry);
        strategy.processPausedExecution(entry);

        verifyNoMoreInteractions(entry);

        strategy.notifyUnPaused(entry);

        verify(entry).scheduleNext(anyLong());
    }
}
