package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task must search objects with target task
 */
public class GetObjectFromCachedCollectionTask implements IDatabaseTask {
    private IStorageConnection connection;
    private IDatabaseTask getItemTask;

    private IField collectionNameField;
    private IField pageSizeField;
    private IField pageNumberField;
    private IField keyNameField;
    private IField keyValueField;
    private IField criteriaEqualsIsActiveField;
    private IField criteriaDateToStartDateTimeField;
    //TODO:: this format should be setted for whole project?
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * @param getItemTask Target task for getting items
     * @throws CreateCachedCollectionTaskException for error during creating
     */
    public GetObjectFromCachedCollectionTask(final IDatabaseTask getItemTask) throws CreateCachedCollectionTaskException {
        this.getItemTask = getItemTask;
        try {
            this.collectionNameField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "collectionName");
            this.keyNameField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "keyName");
            this.keyValueField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "keyValue");
            this.pageSizeField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "pageSize");
            this.pageNumberField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "pageNumber");
            this.criteriaEqualsIsActiveField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "criteria/isActive/$eq");
            this.criteriaDateToStartDateTimeField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "criteria/startDateTime/$date-to");
        } catch (ResolutionException e) {
            throw new CreateCachedCollectionTaskException("Can't create GetObjectFromCachedCollectionTask.", e);
        }
    }

    /**
     * Prepare
     * @param query query object
     *              <pre>
     *              {
     *                  "keyName" : KEY_OF_COLLECTION,
     *                  "keyValue": "VALUE_FOR_KEY",
     *                  "collectionName" : "COLLECTION _NAME"
     *              }    
     *              </pre>
     * Query which would be passed to the nested task:
     * TODO:: change this format after finish task's refactoring
     *              <pre>
     *              {
     *                  "pageSize": 100,
     *                  "pageNumber": 1,
     *                  "collectionName" : "COLLECTION _NAME",
     *                  "criteria": {
     *                      "isActive": {"$eq": true},
     *                      "startDateTime": {"date-to": "now"},
     *                      "<keyName>": {"$eq": "<keyValue>"}
     *                  }
     *              }
     *              </pre>
     * @throws TaskPrepareException Throw when some was incorrect in preparing query
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            IObject queryForNestedTask = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));
            collectionNameField.out(queryForNestedTask, collectionNameField.in(query));
            //TODO:: remove hardcode size
            pageSizeField.out(queryForNestedTask, 100);
            pageNumberField.out(queryForNestedTask, 1);

            criteriaEqualsIsActiveField.out(queryForNestedTask, true);
            criteriaDateToStartDateTimeField.out(queryForNestedTask, LocalDateTime.now().format(FORMATTER));
            String keyName = keyNameField.in(query);
            String keyValue = keyValueField.in(query);
            IField criteriaEqualsKeyField = IOC.resolve(Keys.getOrAdd(IField.class.toString()), keyName + "/$eq/" + keyValue);
            criteriaEqualsKeyField.in(queryForNestedTask);
            getItemTask.prepare(queryForNestedTask);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create searchQuery from input query", e);
        } catch (InvalidArgumentException | ChangeValueException | ReadValueException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    /**
     * @param connection New connection for this and target tasks
     */
    @Override
    public void setConnection(final IStorageConnection connection) {
        this.connection = connection;
        getItemTask.setConnection(connection);
    }

    @Override
    public IStorageConnection getConnection() {
        return connection;
    }

    /**
     * @throws TaskExecutionException Throw when target task can't execute query
     */
    @Override
    public void execute() throws TaskExecutionException {
        getItemTask.execute();
    }
}
