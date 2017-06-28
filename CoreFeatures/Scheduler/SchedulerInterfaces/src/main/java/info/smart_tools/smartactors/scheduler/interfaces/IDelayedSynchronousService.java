package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.isynchronous_service.ISynchronousService;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartupException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;

/**
 * Interface for synchronous services providing methods for delayed start and stop.
 */
public interface IDelayedSynchronousService extends ISynchronousService {
    /**
     * Start this service after given moment of time.
     *
     * @param startTime    the time to start the service
     * @throws ServiceStartupException if any error occurs starting the service or scheduling service start
     * @throws InvalidArgumentException if {@code startTime} is not valid
     * @throws IllegalServiceStateException if the service is already running
     */
    void startAfter(long startTime) throws ServiceStartupException, IllegalServiceStateException, InvalidArgumentException;

    /**
     * Stop this service after given moment of time.
     *
     * @param stopTime    the time to stop the service
     * @throws ServiceStopException if any error occurs stopping the service or scheduling service stop
     * @throws InvalidArgumentException if {@code stopTime} is not valid
     * @throws IllegalServiceStateException if the service is already stopped
     */
    void stopAfter(long stopTime) throws ServiceStopException, IllegalServiceStateException, InvalidArgumentException;
}
