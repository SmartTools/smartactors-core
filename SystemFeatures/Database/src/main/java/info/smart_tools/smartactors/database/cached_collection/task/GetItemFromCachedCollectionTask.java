package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
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
 * Task must search objects with target task
 */
public class GetItemFromCachedCollectionTask implements IDatabaseTask {

    private IDatabaseTask getItemTask;
    private IStorageConnection connection;

    private IField collectionNameField;
    private IField callbackField;
    private IField keyNameField;
    private IField keyValueField;
    private IField keyField;
    private IField isActiveField;
    private IField equalsField;
    private IField filterField;
    private IField dateToField;
    private IField startDateTimeField;

    private DateTimeFormatter formatter;

    /**
     * @param connection storage connection for executing query
     * @throws CreateCachedCollectionTaskException for error during creating
     */
    public GetItemFromCachedCollectionTask(final IStorageConnection connection) throws CreateCachedCollectionTaskException {
        this.connection = connection;
        try {
            this.formatter = IOC.resolve(Keys.getKeyByName("datetime_formatter"));
            this.collectionNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "collectionName");
            this.keyNameField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "keyName");
            this.keyValueField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "key");
            this.callbackField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "callback");
            this.isActiveField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "isActive");
            this.equalsField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "$eq");
            this.filterField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "filter");
            this.dateToField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "$date-to");
            this.startDateTimeField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "startDateTime");
        } catch (ResolutionException e) {
            throw new CreateCachedCollectionTaskException("Can't create GetItemFromCachedCollectionTask.", e);
        }
    }

    /**
     * Prepare
     * @param query query object
     *              <pre>
     *              {
     *                  "keyName" : KEY_OF_COLLECTION,
     *                  "key": "VALUE_FOR_KEY",
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
     *                  "criteria":
     *                      "filter" :    [
     *                          {"isActive": {"$eq": true}},
     *                          {"startDateTime": {"date-to": "now"}},
     *                          {"{@literal <}keyName{@literal >}": {"$eq": "{@literal <}keyValue{@literal >}"}}
     *                      ]
     *              }
     *              </pre>
     * @throws TaskPrepareException Throw when some was incorrect in preparing query
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            IObject queryForNestedTask  = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            IObject filterObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            keyField = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), (String) keyNameField.in(query));
            String keyValue = keyValueField.in(query);

            IObject eqKeyObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            equalsField.out(eqKeyObject, keyValue);
            keyField.out(filterObject, eqKeyObject);

            IObject isActiveObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            equalsField.out(isActiveObject, true);
            isActiveField.out(filterObject, isActiveObject);

            IObject dateObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            dateToField.out(dateObject, LocalDateTime.now().format(formatter));
            startDateTimeField.out(filterObject, dateObject);

            filterField.out(queryForNestedTask, filterObject);

            getItemTask = IOC.resolve(
                Keys.getKeyByName("db.collection.search"),
                connection,
                collectionNameField.in(query),
                queryForNestedTask,
                callbackField.in(query)
            );

        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create searchQuery from input query", e);
        } catch (InvalidArgumentException | ChangeValueException | ReadValueException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    /**
     * @throws TaskExecutionException Throw when target task can't execute query
     */
    @Override
    public void execute() throws TaskExecutionException {
        getItemTask.execute();
    }
}
