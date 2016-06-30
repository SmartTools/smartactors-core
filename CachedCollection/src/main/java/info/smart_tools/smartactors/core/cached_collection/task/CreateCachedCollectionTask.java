package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.wrapper.CreateCachedCollectionQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates cached collection with defined collection name and key field
 */
public class CreateCachedCollectionTask implements IDatabaseTask {

    private static final String ORDERED_INDEX = "ordered";
    private static final String DATE_TIME_INDEX = "datetime";

    private IDatabaseTask createCollectionTask;

    /**
     * Constructor
     * @param createCollectionTask nested task for creating collection
     */
    public CreateCachedCollectionTask(final IDatabaseTask createCollectionTask) {
        this.createCollectionTask = createCollectionTask;
    }

    /**
     * Prepares database query
     *
     * @param query query object
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            CreateCachedCollectionQuery message = IOC.resolve(Keys.getOrAdd(CreateCachedCollectionQuery.class.toString()), query);
            Map<String, String> indexes = new HashMap<>();
            indexes.put(message.getKey(), ORDERED_INDEX);
            //TODO:: Should it be ordered?
            indexes.put("isActive", ORDERED_INDEX);
            indexes.put("startDateTime", DATE_TIME_INDEX);
            message.setIndexes(indexes);

            createCollectionTask.prepare(message.wrapped());
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create message from query", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for create cached collection", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        createCollectionTask.setConnection(connection);
    }

    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs in process of task execution
     */
    @Override
    public void execute() throws TaskExecutionException {
        createCollectionTask.execute();
    }
}
