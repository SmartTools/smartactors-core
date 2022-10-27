package info.smart_tools.smartactors.scheduler.actor.impl.refresher;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.scheduler.interfaces.IDelayedSynchronousService;

/**
 * Interface for a service performing synchronization between remote and local entry storages.
 */
public interface ISchedulerStorageRefresher extends IDelayedSynchronousService {
    /**
     * Update service configuration.
     *
     * @param config    object containing new parameters
     * @throws ReadValueException if error occurs reading parameter values
     * @throws InvalidArgumentException if parameters are not valid
     */
    void configure(IObject config) throws ReadValueException, InvalidArgumentException;
}
