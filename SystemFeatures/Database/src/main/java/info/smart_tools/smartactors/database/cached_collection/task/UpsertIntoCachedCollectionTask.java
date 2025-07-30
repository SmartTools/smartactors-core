package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.field.nested_field.NestedField;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Makes add or update operation for cached collection
 */
public class UpsertIntoCachedCollectionTask implements IDatabaseTask {

    private IDatabaseTask upsertTask;
    private IStorageConnection connection;

    private IField startDateTimeField;
    private IField collectionNameField;
    private IField documentField;
    private DateTimeFormatter formatter;

    /**
     * @param connection storage connection for executing query
     * @throws CreateCachedCollectionTaskException for create task error
     */
    public UpsertIntoCachedCollectionTask(final IStorageConnection connection) throws CreateCachedCollectionTaskException {
        this.connection = connection;
        try {
            this.formatter = IOC.resolve(Keys.getKeyByName("datetime_formatter"));
            this.startDateTimeField = IOC.resolve(Keys.getKeyByName(NestedField.class.getCanonicalName()), "document/startDateTime");
            this.collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            this.documentField  = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "document");
        } catch (ResolutionException e) {
            throw new CreateCachedCollectionTaskException("Can't create UpsertIntoCachedCollectionTask.", e);
        }
    }

    /**
     * Prepares database query
     * @param query query object
     *              <pre>
     *              {
     *                  "document" : {CACHED ITEM},
     *                  "collectionName" : "COLLECTION_NAME"
     *              }
     *              </pre>
     * The same query would be passed to the nested task's prepare method,
     * but startDateTime field in document would be set to the current time.
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            if (startDateTimeField.in(query) == null) {
                startDateTimeField.out(query, LocalDateTime.now().format(formatter));
            }
            upsertTask = IOC.resolve(
                Keys.getKeyByName("db.collection.upsert"),
                connection,
                collectionNameField.in(query),
                documentField.in(query)
            );
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException | ResolutionException e) {
            throw new TaskPrepareException("Can't prepare query for upsert into cached collection", e);
        }
    }

    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs in process of task execution
     */
    @Override
    public void execute() throws TaskExecutionException {
        upsertTask.execute();
    }
}
