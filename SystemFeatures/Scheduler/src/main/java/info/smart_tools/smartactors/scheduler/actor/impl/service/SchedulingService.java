package info.smart_tools.smartactors.scheduler.actor.impl.service;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.scheduler.actor.impl.refresher.ISchedulerStorageRefresher;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;

/**
 *
 */
public class SchedulingService implements ISchedulerService {
    private final IDelayedSynchronousService timerService;
    private final ISchedulerStorageRefresher refreshService;
    private final ISchedulerEntryStorage entryStorage;

    public SchedulingService(
            final IDelayedSynchronousService timerService,
            final ISchedulerStorageRefresher refreshService,
            final ISchedulerEntryStorage entryStorage) {
        this.timerService = timerService;
        this.refreshService = refreshService;
        this.entryStorage = entryStorage;
    }

    @Override
    public void start() throws IllegalServiceStateException, ServiceStartException {
        timerService.start();
        refreshService.start();
    }

    @Override
    public void stop() throws IllegalServiceStateException, ServiceStopException {
        refreshService.stop();
        timerService.stop();
    }

    @Override
    public void startAfter(final long startTime)
            throws ServiceStartException, IllegalServiceStateException, InvalidArgumentException {
        timerService.startAfter(startTime);
        refreshService.startAfter(startTime);
    }

    @Override
    public void stopAfter(final long stopTime)
            throws ServiceStopException, IllegalServiceStateException, InvalidArgumentException {
        refreshService.stopAfter(stopTime);
        timerService.stopAfter(stopTime);
    }

    @Override
    public ISchedulerEntryStorage getEntryStorage() {
        return entryStorage;
    }

    @Override
    public void configure(final IObject config) throws ReadValueException, InvalidArgumentException {
        refreshService.configure(config);
    }
}
