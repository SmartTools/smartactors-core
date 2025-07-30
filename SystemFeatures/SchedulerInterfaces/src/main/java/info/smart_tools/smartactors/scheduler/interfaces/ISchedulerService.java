package info.smart_tools.smartactors.scheduler.interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Service providing an entry storage and execution and synchronization of entries stored there.
 */
public interface ISchedulerService extends IDelayedSynchronousService {
    /**
     * @return the entry storage of this service
     */
    ISchedulerEntryStorage getEntryStorage();

    /**
     * Update service configuration.
     *
     * @param config    object containing new parameters
     * @throws ReadValueException if error occurs reading parameter values
     * @throws InvalidArgumentException if parameters are not valid
     */
    void configure(IObject config) throws ReadValueException, InvalidArgumentException;
}
