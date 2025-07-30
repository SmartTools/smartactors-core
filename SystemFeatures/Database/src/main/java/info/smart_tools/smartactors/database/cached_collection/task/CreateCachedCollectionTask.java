package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates cached collection with defined collection name and key field
 */
public class CreateCachedCollectionTask implements IDatabaseTask {

    private static final String ORDERED_INDEX = "ordered";
    private static final String DATE_TIME_INDEX = "datetime";

    private IField keyNameField;
    private IField indexesField;

    private IDatabaseTask createCollectionTask;

    /**
     * Constructor
     * @param createCollectionTask nested task for creating collection
     * @throws CreateCachedCollectionTaskException for create task error
     */
    public CreateCachedCollectionTask(final IDatabaseTask createCollectionTask) throws CreateCachedCollectionTaskException {
        this.createCollectionTask = createCollectionTask;
        try {
            this.keyNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "keyName");
            this.indexesField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "indexes");
        } catch (ResolutionException e) {
            throw new CreateCachedCollectionTaskException("Can't create CreateCachedCollectionTask.", e);
        }
    }

    /**
     * Prepares database query
     * @param query query object
     *              {
     *              "keyName": "name for key of this collection"
     *              }
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            Map<String, String> indexes = new HashMap<>();
            indexes.put(keyNameField.in(query), ORDERED_INDEX);
            indexes.put("isActive", ORDERED_INDEX);
            indexes.put("startDateTime", DATE_TIME_INDEX);
            indexesField.out(query, indexes);

            createCollectionTask.prepare(query);
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for create cached collection", e);
        }
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
