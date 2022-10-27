package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.query_executors;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.base.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.IQueryExecutor;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutorInitializationException;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DatabaseCountQueryExecutor implements IQueryExecutor {
    private final IFieldName collectionFieldName;
    private final IFieldName connectionOptionsDependencyFieldName;
    private final IFieldName connectionPoolDependencyFieldName;
    private final IFieldName filterFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public DatabaseCountQueryExecutor()
            throws ResolutionException {
        collectionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "collection");
        connectionOptionsDependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "connectionOptionsDependency");
        connectionPoolDependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "connectionPoolDependency");
        filterFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "filter");
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws QueryExecutorInitializationException {
        try {
            entry.getState().setValue(collectionFieldName, args.getValue(collectionFieldName));
            entry.getState().setValue(connectionOptionsDependencyFieldName, args.getValue(connectionOptionsDependencyFieldName));
            entry.getState().setValue(connectionPoolDependencyFieldName, args.getValue(connectionPoolDependencyFieldName));
            entry.getState().setValue(filterFieldName, args.getValue(filterFieldName));
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new QueryExecutorInitializationException(e);
        }
    }

    @Override
    public Collection<? extends Number> execute(final ISchedulerEntry entry) throws QueryExecutionException {
        try {
            List<Long> res = new ArrayList<>(1);
            Object connectionOptions = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), entry.getState().getValue(connectionOptionsDependencyFieldName)));
            IPool connectionPool = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), entry.getState().getValue(connectionPoolDependencyFieldName)),
                    connectionOptions
            );

            try (PoolGuard pg = new PoolGuard(connectionPool)) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.count"),
                        pg.getObject(),
                        entry.getState().getValue(collectionFieldName),
                        entry.getState().getValue(filterFieldName),
                        (IAction<Long>) res::add
                );

                task.execute();
            }

            return res;
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | PoolGuardException | TaskExecutionException e) {
            throw new QueryExecutionException(e);
        }
    }
}
