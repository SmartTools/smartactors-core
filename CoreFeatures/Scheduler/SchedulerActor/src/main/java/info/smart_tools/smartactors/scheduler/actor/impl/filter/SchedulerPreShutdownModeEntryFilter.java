package info.smart_tools.smartactors.scheduler.actor.impl.filter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryFilter;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerEntryFilterException;

/**
 *
 */
public class SchedulerPreShutdownModeEntryFilter implements ISchedulerEntryFilter {
    private final IFieldName preShutdownExecFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public SchedulerPreShutdownModeEntryFilter()
            throws ResolutionException {
        preShutdownExecFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "preShutdownExec");
    }

    @Override
    public boolean testExec(final ISchedulerEntry entry) throws SchedulerEntryFilterException {
        return checkPreShutdownExecutionEnabled(entry.getState());
    }

    @Override
    public boolean testAwake(final ISchedulerEntry entry) throws SchedulerEntryFilterException {
        return checkPreShutdownExecutionEnabled(entry.getState());
    }

    @Override
    public boolean testRestore(final IObject entryState) throws SchedulerEntryFilterException {
        return checkPreShutdownExecutionEnabled(entryState);
    }

    private boolean checkPreShutdownExecutionEnabled(final IObject state)
            throws SchedulerEntryFilterException {
        Object flag;
        try {
            flag = state.getValue(preShutdownExecFieldName);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new SchedulerEntryFilterException(e);
        }

        try {
            return flag == null ? false : (Boolean) flag;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
