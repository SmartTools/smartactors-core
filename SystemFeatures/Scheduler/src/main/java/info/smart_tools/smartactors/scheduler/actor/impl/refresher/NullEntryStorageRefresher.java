package info.smart_tools.smartactors.scheduler.actor.impl.refresher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Null-implementation of storage refresh service.
 */
public class NullEntryStorageRefresher implements ISchedulerStorageRefresher {
    @Override
    public void start() throws IllegalServiceStateException, ServiceStartException {

    }

    @Override
    public void stop() throws IllegalServiceStateException, ServiceStopException {

    }

    @Override
    public void startAfter(final long startTime) throws ServiceStartException, IllegalServiceStateException, InvalidArgumentException {

    }

    @Override
    public void stopAfter(final long stopTime) throws ServiceStopException, IllegalServiceStateException, InvalidArgumentException {

    }

    @Override
    public void configure(final IObject config) throws ReadValueException, InvalidArgumentException {

    }
}
