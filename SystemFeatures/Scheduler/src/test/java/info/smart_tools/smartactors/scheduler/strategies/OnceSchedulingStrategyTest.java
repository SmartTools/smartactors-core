package info.smart_tools.smartactors.scheduler.strategies;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Test for {@link OnceSchedulingStrategy}.
 */
public class OnceSchedulingStrategyTest extends IOCInitializer {
    private ISchedulerEntry entry;
    private IFieldName time;
    private IFieldName save;
    private IFieldName ntl;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        entry = mock(ISchedulerEntry.class);
        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));

        time = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "time");
        save = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "save");
        ntl = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "neverTooLate");
    }

    @Test
    public void Should_cancelEntryWhenErrorOccurs()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        strategy.processException(entry, mock(Throwable.class));

        verify(entry).cancel();
    }

    @Test
    public void Should_cancelEntryWhenItIsDone()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        strategy.postProcess(entry);

        verify(entry).cancel();
    }

    @Test
    public void Should_initializeEntryAndSaveIfNecessary()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        strategy.init(entry, IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'time':'1989-09-11T00:00:05','save':true,'neverTooLate':true}".replace('\'','"')));

        verify(entry).save();
        verify(entry).scheduleNext(LocalDateTime.parse("1989-09-11T00:00:05").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());

        assertEquals("1989-09-11T00:00:05", entry.getState().getValue(time));
        assertEquals(true, entry.getState().getValue(ntl));
        assertNull(entry.getState().getValue(save));
    }

    @Test
    public void Should_initializeEntryAndDoNotSaveItIfNotNecessary()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        strategy.init(entry, IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'time':'1989-09-11T00:00:05','save':false}".replace('\'','"')));

        verify(entry, times(0)).save();
        verify(entry).scheduleNext(LocalDateTime.parse("1989-09-11T00:00:05").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());

        assertEquals("1989-09-11T00:00:05", entry.getState().getValue(time));
        assertNull(entry.getState().getValue(ntl));
        assertNull(entry.getState().getValue(save));
    }

    @Test
    public void Should_restoreEntryAndCancelItWhenItIsTooLateWhenItMayBeTooLate()
            throws Exception {
        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'time':'2000-10-10T00:50:15','neverTooLate':false}".replace('\'','"')));

        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        strategy.restore(entry);

        verify(entry).cancel();
    }

    @Test
    public void Should_restoreEntryAndScheduleItWhenItIsTooLateButItIsNeverTooLate()
            throws Exception {
        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'time':'2000-10-10T00:50:15','neverTooLate':true}".replace('\'','"')));

        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        strategy.restore(entry);

        verify(entry).scheduleNext(LocalDateTime.parse("2000-10-10T00:50:15").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());
    }

    @Test
    public void Should_restoreEntryAndRescheduleItWhenItIsNotTooLate()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'time':'3410-01-01T00:00:01','neverTooLate':true}".replace('\'','"')));

        strategy.restore(entry);

        verify(entry).scheduleNext(LocalDateTime.parse("3410-01-01T00:00:01").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());

        reset(entry);

        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'time':'3410-01-01T00:00:01','neverTooLate':false}".replace('\'','"')));

        strategy.restore(entry);

        verify(entry).scheduleNext(LocalDateTime.parse("3410-01-01T00:00:01").atZone(ZoneOffset.UTC).toInstant().toEpochMilli());
    }

    @Test
    public void Should_cancelPausedEntryWhenItIsNotNeverTooLate()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                "{'time':'3410-01-01T00:00:01','neverTooLate':false}".replace('\'','"')));

        strategy.notifyPaused(entry);
        strategy.processPausedExecution(entry);
        strategy.notifyUnPaused(entry);

        verify(entry).cancel();
    }

    @Test
    public void Should_reschedulePausedEntryWhenItIsNeverTooLate()
            throws Exception {
        ISchedulingStrategy strategy = new OnceSchedulingStrategy();

        when(entry.getState()).thenReturn(IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()),
                "{'time':'3410-01-01T00:00:01','neverTooLate':true}".replace('\'','"')));

        strategy.notifyPaused(entry);
        strategy.processPausedExecution(entry);
        strategy.notifyUnPaused(entry);

        verify(entry, times(0)).cancel();
        verify(entry).scheduleNext(anyLong());
    }
}
