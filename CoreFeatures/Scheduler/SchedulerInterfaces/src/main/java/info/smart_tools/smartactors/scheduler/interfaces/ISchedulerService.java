package info.smart_tools.smartactors.scheduler.interfaces;

/**
 * Service providing an entry storage and execution and synchronization of entries stored there.
 */
public interface ISchedulerService extends IDelayedSynchronousService {
    /**
     * @return the entry storage of this service
     */
    ISchedulerEntryStorage getEntryStorage();
}
