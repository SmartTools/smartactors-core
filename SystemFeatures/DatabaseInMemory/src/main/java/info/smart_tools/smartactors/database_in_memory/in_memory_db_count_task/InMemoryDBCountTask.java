package info.smart_tools.smartactors.database_in_memory.in_memory_db_count_task;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.interfaces.idatabase.IDatabase;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * The database task which is to count documents in {@link InMemoryDatabase} by multiple search criteria.
 * The search criteria are passed in the complex IObject to the {@link #prepare(IObject)} method.
 * <p>
 * The example of prepare object:
 * {@code
 *   {
 *       "collectionName": CollectionName object,
 *       "criteria": {
 *           "filter": {
 *               "$or": [
 *                   { "a": { "$eq": "b" } },
 *                   { "b": { "$gt": 42 } }
 *               ]
 *           }
 *       },
 *       "callback": IAction callback to receive count
 *   }
 * }
 * </p>
 * <p>
 * The criteria is the IObject which contains a set of conditions and operators.
 * Conditions joins operators together.
 * Operators match the specified document field against the specified criteria.
 * </p>
 *
 * Available conditions:
 * <ul>
 * <li>{@code $and} — ANDs operators and nested conditions</li>
 * <li>{@code $or} — ORs operators and nested conditions</li>
 * <li>{@code $not} — negate all nested operators and conditions, is equivalent to NOT(conditionA AND conditionB)</li>
 * </ul>
 * Available operators:
 * <ul>
 * <li>{@code $eq} — test for equality of the document field and the specified value</li>
 * <li>{@code $neq} — test for not equality</li>
 * <li>{@code $lt} — "less than", the document field is less than the specified value</li>
 * <li>{@code $gt} — "greater than", the document field is larger than the specified value</li>
 * <li>{@code $lte} — less or equal</li>
 * <li>{@code $gte} — greater or equal</li>
 * <li>{@code $isNull} — checks for null if the specified value is "true" or checks for not null if "false"</li>
 * <li>{@code $date-from} — greater or equal for datetime fields</li>
 * <li>{@code $date-to} — less or equal for datetime fields</li>
 * <li>{@code $in} — checks for equality to any of the specified values in the array</li>
 * <li>{@code $hasTag} — check the document field is JSON document contains the specified value as field name or value</li>
 * </ul>
 *
 */

public class InMemoryDBCountTask implements IDatabaseTask {

    private IFieldName collectionFieldName;
    private IFieldName criteriaFieldName;
    private IFieldName callbackFieldName;

    /**
     * Collection where the document should be counted.
     */
    private String collection;
    /**
     * Criteria to count the documents.
     */
    private IObject criteria;
    /**
     * Callback function to call when the documents are counted.
     */
    private IAction<Long> callback;

    /**
     * Creates the task.
     * @throws TaskPrepareException if cannot resolve IFieldName
     */
    public InMemoryDBCountTask() throws TaskPrepareException {
        try {
            collectionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "collectionName");
            criteriaFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "criteria");
            callbackFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "callback");
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Failed to resolve \"IFieldName\"", e);
        }
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            collection = (String) query.getValue(collectionFieldName);
            criteria = (IObject) query.getValue(criteriaFieldName);
            callback = (IAction<Long>) query.getValue(callbackFieldName);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Failed to getting values from query", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            IDatabase dataBase = IOC.resolve(Keys.getKeyByName(InMemoryDatabase.class.getCanonicalName()));
            Long result = dataBase.count(criteria, collection);
            callback.execute(result);
        } catch (ResolutionException e) {
            throw new TaskExecutionException("Failed to resolve InMemoryDatabase", e);
        } catch (Exception e) {
            try {
                throw new TaskExecutionException("Count failed: criteria = " + criteria.serialize(), e);
            } catch (SerializeException | NullPointerException e1) {
                throw new TaskExecutionException("Count failed", e);
            }
        }
    }

}
