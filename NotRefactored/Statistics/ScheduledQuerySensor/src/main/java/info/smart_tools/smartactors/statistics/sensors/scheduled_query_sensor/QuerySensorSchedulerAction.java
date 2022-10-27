package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerAction;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionExecutionException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulerActionInitializationException;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.IQueryExecutor;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions.QueryExecutorInitializationException;

import java.util.Collection;

/**
 * Scheduler action that queries data using {@link IQueryExecutor a query executor} and sends it to a statistics chain.
 */
public class QuerySensorSchedulerAction implements ISchedulerAction {
    private final IFieldName queryExecutorFieldName;
    private final IFieldName statisticsChainFieldName;
    private final IFieldName dataFieldName;
    private final IFieldName periodStartFieldName;
    private final IFieldName periodEndFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public QuerySensorSchedulerAction()
            throws ResolutionException {
        queryExecutorFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queryExecutor");
        statisticsChainFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "statisticsChain");
        dataFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "data");
        periodStartFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "periodStart");
        periodEndFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "periodEnd");
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulerActionInitializationException {
        try {
            Object queryExecutorDependency = args.getValue(queryExecutorFieldName);
            IQueryExecutor queryExecutor = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), queryExecutorDependency));

            queryExecutor.init(entry, args);

            entry.getState().setValue(queryExecutorFieldName, queryExecutorDependency);
            entry.getState().setValue(statisticsChainFieldName, args.getValue(statisticsChainFieldName));
        } catch (ResolutionException | ReadValueException | ChangeValueException | InvalidArgumentException
                | QueryExecutorInitializationException e) {
            throw new SchedulerActionInitializationException("Error occurred initializing query sensor scheduler action.", e);
        }
    }

    @Override
    public void execute(final ISchedulerEntry entry) throws SchedulerActionExecutionException {
        try {
            Object queryExecutorDependency = entry.getState().getValue(queryExecutorFieldName);
            IQueryExecutor queryExecutor = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), queryExecutorDependency));

            Collection<? extends Number> data = queryExecutor.execute(entry);
            Long time = entry.getLastTime();

            IObject message = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            message.setValue(dataFieldName, data);
            message.setValue(periodStartFieldName, time);
            message.setValue(periodEndFieldName, time);

            //Object chainId = IOC.resolve(Keys.getKeyByName("chain_id_from_map_name_and_message"), entry.getState().getValue(statisticsChainFieldName), message);
            //MessageBus.send(message, chainId);
            MessageBus.send(message, entry.getState().getValue(statisticsChainFieldName));
        } catch (ReadValueException | InvalidArgumentException | QueryExecutionException | ResolutionException | ChangeValueException
                | SendingMessageException e) {
            throw new SchedulerActionExecutionException("Error occurred querying or sending statistics.", e);
        }
    }
}
