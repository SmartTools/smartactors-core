package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutorInitializationException;

import java.util.Collection;

/**
 * Interface for an object executing query.
 */
public interface IQueryExecutor {
    /**
     * Prepare the scheduler entry the query will be executed by.
     *
     * @param entry    the entry
     * @param args     entry creation arguments
     * @throws QueryExecutorInitializationException if any error occurs
     */
    void init(ISchedulerEntry entry, IObject args) throws QueryExecutorInitializationException;

    /**
     * Execute the query.
     *
     * @param entry    the entry
     * @return the query result as collection of numbers
     * @throws QueryExecutionException if any error occurs
     */
    Collection<? extends Number> execute(ISchedulerEntry entry) throws QueryExecutionException;
}
