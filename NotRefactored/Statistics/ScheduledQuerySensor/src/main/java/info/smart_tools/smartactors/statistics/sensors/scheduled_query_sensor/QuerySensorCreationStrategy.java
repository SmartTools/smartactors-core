package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;

/**
 *
 */
public class QuerySensorCreationStrategy implements IStrategy {
    private static final int CHAIN_ID_STRATEGY_ARGUMENT_INDEX = 0;
    private static final int CONFIG_STRATEGY_ARGUMENT_INDEX = 1;

    private static final String ACTION_DEPENDENCY = "query sensor scheduler action";

    private final IFieldName actionFieldName;
    private final IFieldName statisticsChainFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public QuerySensorCreationStrategy()
            throws ResolutionException {
        actionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "action");
        statisticsChainFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "statisticsChain");
    }

    @Override
    public <T> T resolve(final Object... args) throws StrategyException {
        Object statisticsChainId = args[CHAIN_ID_STRATEGY_ARGUMENT_INDEX];
        IObject conf = (IObject) args[CONFIG_STRATEGY_ARGUMENT_INDEX];

        try {
            conf.setValue(statisticsChainFieldName, statisticsChainId);
            conf.setValue(actionFieldName, ACTION_DEPENDENCY);

            ISchedulerEntry entry = IOC.resolve(
                    Keys.getKeyByName("new scheduler entry"),
                    conf,
                    IOC.resolve(Keys.getKeyByName("query sensors scheduler storage"))
            );

            return (T) new QuerySensorHandle(entry);
        } catch (ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new StrategyException(e);
        }
    }
}
