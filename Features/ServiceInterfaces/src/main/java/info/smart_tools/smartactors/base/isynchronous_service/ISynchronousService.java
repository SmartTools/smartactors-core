package info.smart_tools.smartactors.base.isynchronous_service;

import info.smart_tools.smartactors.base.isynchronous_service.exceptions.IllegalServiceStateException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStartException;
import info.smart_tools.smartactors.base.isynchronous_service.exceptions.ServiceStopException;

/**
 * Interface for objects representing some service and providing methods to manage it synchronously.
 */
public interface ISynchronousService {
    /**
     * Start the service.
     *
     * @throws IllegalServiceStateException if the service is already started
     * @throws ServiceStartException if any error occurs starting the service
     */
    void start() throws IllegalServiceStateException, ServiceStartException;

    /**
     * Stop the service.
     *
     * @throws IllegalServiceStateException if the service is not started
     * @throws ServiceStopException if any error occurs stopping the service
     */
    void stop() throws IllegalServiceStateException, ServiceStopException;
}
