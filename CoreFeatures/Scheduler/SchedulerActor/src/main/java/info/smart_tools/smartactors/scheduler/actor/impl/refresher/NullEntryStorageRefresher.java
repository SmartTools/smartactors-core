package info.smart_tools.smartactors.scheduler.actor.impl.refresher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;

/**
 * Null-implementation of storage refresh service.
 */
public class NullEntryStorageRefresher implements IDelayedSynchronousService {
    @Override
    public void start() throws IllegalServiceStateException, ServiceStartupException {

    }

    @Override
    public void stop() throws IllegalServiceStateException, ServiceStopException {

    }

    @Override
    public void startAfter(final long startTime) throws ServiceStartupException, IllegalServiceStateException, InvalidArgumentException {

    }

    @Override
    public void stopAfter(final long stopTime) throws ServiceStopException, IllegalServiceStateException, InvalidArgumentException {

    }
}
