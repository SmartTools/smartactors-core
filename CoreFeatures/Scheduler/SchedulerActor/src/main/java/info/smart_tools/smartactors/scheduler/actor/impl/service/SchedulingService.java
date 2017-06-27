package info.smart_tools.smartactors.scheduler.actor.impl.service;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntryStorage;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerService;

/**
 *
 */
public class SchedulingService implements ISchedulerService {
    private final IDelayedSynchronousService timerService;
    private final IDelayedSynchronousService refreshService;
    private final ISchedulerEntryStorage entryStorage;

    public SchedulingService(
            final IDelayedSynchronousService timerService,
            final IDelayedSynchronousService refreshService,
            final ISchedulerEntryStorage entryStorage) {
        this.timerService = timerService;
        this.refreshService = refreshService;
        this.entryStorage = entryStorage;
    }

    @Override
    public void start() throws IllegalServiceStateException, ServiceStartupException {
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
            throws ServiceStartupException, IllegalServiceStateException, InvalidArgumentException {
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
}
