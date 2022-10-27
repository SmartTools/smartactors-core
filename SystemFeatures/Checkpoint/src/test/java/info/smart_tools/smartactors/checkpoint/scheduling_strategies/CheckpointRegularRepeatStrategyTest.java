package info.smart_tools.smartactors.checkpoint.scheduling_strategies;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test for {@link CheckpointRegularRepeatStrategy}.
 */
public class CheckpointRegularRepeatStrategyTest extends IOCInitializer {
    private ISchedulerEntry entryMock;
    private IObject entryState;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {

    }

    private void makeEntry(final String stateSrc) throws Exception {
        entryMock = mock(ISchedulerEntry.class);
        entryState = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), stateSrc.replace('\'','"'));
        when(entryMock.getState()).thenReturn(entryState);
    }

    private IObject makeArgs(final String src) throws Exception {
        return IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), src.replace('\'','"'));
    }

    @Test
    public void Should_copyArgumentsToEntryState()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{}");

        strategy.init(entryMock, makeArgs("{" +
                "'times':2," +
                "'interval':'PT3H'" +
                "}"));

        verify(entryMock).save();
        verify(entryMock).scheduleNext(anyLong());

        assertEquals("PT3H", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "interval")));
        assertEquals("PT3H", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "postRestoreDelay")));
        assertEquals("PT3H", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "postCompletionDelay")));
        assertEquals(2, entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "times")));
        assertEquals(1, entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "remainingTimes")));

        //
        makeEntry("{}");

        strategy.init(entryMock, makeArgs("{" +
                "'times':3," +
                "'interval':'PT2H'," +
                "'postRestoreDelay':'PT3H'," +
                "'postCompletionDelay':'PT4H'" +
                "}"));

        verify(entryMock).save();
        verify(entryMock).scheduleNext(anyLong());

        assertEquals("PT2H", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "interval")));
        assertEquals("PT3H", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "postRestoreDelay")));
        assertEquals("PT4H", entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "postCompletionDelay")));
        assertEquals(3, entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "times")));
        assertEquals(2, entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "remainingTimes")));
    }

    @Test
    public void Should_notSaveEntryWith0TimesToRepeat()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{}");

        strategy.init(entryMock, makeArgs("{" +
                "'times':0," +
                "'interval':'PT3H'" +
                "}"));

        verify(entryMock, never()).save();
        verify(entryMock, never()).scheduleNext(anyLong());
    }

    @Test
    public void Should_cancelCompletedEntryWithNoTrialsRemaining()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{" +
                "'completed':true," +
                "'remainingTimes':0}");

        strategy.postProcess(entryMock);

        verify(entryMock).cancel();
        verify(entryMock, never()).save();
    }

    @Test
    public void Should_saveCompletedEntryForPostCompletionDelay()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{" +
                "'completed':true," +
                "'remainingTimes':1," +
                "'postCompletionDelay':'PT2M'}");

        when(entryMock.getLastTime()).thenReturn(13666L);

        strategy.postProcess(entryMock);

        verify(entryMock).scheduleNext(eq(13666L + Duration.parse("PT2M").toMillis()));
        verify(entryMock).save();
    }

    @Test
    public void Should_markEntryAsCompletedAndSaveItForPostCompletionDelayIfItRunsOutOfTrials()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{" +
                "'remainingTimes':0," +
                "'postCompletionDelay':'PT2M'}");

        when(entryMock.getLastTime()).thenReturn(13666L);

        strategy.postProcess(entryMock);

        verify(entryMock).scheduleNext(eq(13666L + Duration.parse("PT2M").toMillis()));
        verify(entryMock).save();

        assertNotNull(entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "completed")));
    }

    @Test
    public void Should_rescheduleEntryInIntervalWhenThereAreTrialsRemainingAndTheEntryIsNotCompleted()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{" +
                "'remainingTimes':1," +
                "'interval':'PT2M4S'}");

        when(entryMock.getLastTime()).thenReturn(13666L);

        strategy.postProcess(entryMock);

        verify(entryMock).scheduleNext(eq(13666L + Duration.parse("PT2M4S").toMillis()));
        verify(entryMock).save();

        assertNull(entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "completed")));
    }

    @Test
    public void Should_applyPostCompletionDelayWhenRestoresCompletedEntry()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{" +
                "'completed':true," +
                "'remainingTimes':1," +
                "'postCompletionDelay':'PT55M3S'}");

        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(long.class);

        long startTime = System.currentTimeMillis();
        strategy.restore(entryMock);

        verify(entryMock).scheduleNext(timeCaptor.capture());

        assertTrue(Math.abs(startTime + Duration.parse("PT55M3S").toMillis() - timeCaptor.getValue()) < 100);
        assertEquals(0, entryState.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "remainingTimes")));
    }

    @Test
    public void Should_applyPostRestoreDelayWhenRestoresNotCompletedEntry()
            throws Exception {
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{" +
                "'remainingTimes':1," +
                "'postRestoreDelay':'PT55M3S'}");

        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(long.class);

        long startTime = System.currentTimeMillis();
        strategy.restore(entryMock);

        verify(entryMock).scheduleNext(timeCaptor.capture());

        assertTrue(Math.abs(startTime + Duration.parse("PT55M3S").toMillis() - timeCaptor.getValue()) < 100);
    }

    @Test
    public void Should_cancelEntryWhenExceptionOccurs()
            throws Exception {
        // TODO: Should it work another way ?
        ISchedulingStrategy strategy = new CheckpointRegularRepeatStrategy();

        makeEntry("{}");

        strategy.processException(entryMock, new Throwable());

        verify(entryMock).cancel();
    }
}
