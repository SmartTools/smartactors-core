package info.smart_tools.smartactors.scheduler.actor.impl.filter;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryFilterException;

/**
 * {@link ISchedulerEntryFilter Entry filter} that permits all entry state changes.
 */
public enum AllPassEntryFilter implements ISchedulerEntryFilter { INSTANCE;
    @Override
    public boolean testExec(final ISchedulerEntry entry) throws SchedulerEntryFilterException {
        return true;
    }

    @Override
    public boolean testAwake(final ISchedulerEntry entry) throws SchedulerEntryFilterException {
        return true;
    }

    @Override
    public boolean testRestore(final IObject entryState) throws SchedulerEntryFilterException {
        return true;
    }
}
