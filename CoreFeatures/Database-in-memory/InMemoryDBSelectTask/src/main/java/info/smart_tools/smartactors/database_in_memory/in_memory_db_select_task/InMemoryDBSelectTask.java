package info.smart_tools.smartactors.database_in_memory.in_memory_db_select_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.interfaces.idatabase.IDatabase;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * The database task which is able to select documents from {@link InMemoryDatabase} by multiple search criteria.
 * The search criteria are passed in the complex IObject to the {@link #prepare(IObject)} method.
 * <p>
 * The example of prepare object:
 * <pre>
 *  {
 *      "collectionName": CollectionName object,
 *      "criteria": {
 *          "filter": {
 *              "$or": [
 *                  { "a": { "$eq": "b" } },
 *                  { "b": { "$gt": 42 } }
 *              ]
 *          },
 *          "page": {
 *              "size": 50,
 *              "number": 2
 *          },
 *          "sort": [
 *              { "a": "asc" },
 *              { "b": "desc" }
 *          ]
 *      },
 *      "callback": IAction callback to receive found documents
 *  }
 *     </pre>
 * </p>
 * <p>
 * The criteria is the IObject which contains a set of conditions and operators.
 * Conditions joins operators together.
 * Operators match the specified document field against the specified criteria.
 * </p>
 * <p>
 * Available conditions:
 * <ul>
 * <li><code>$and</code> — ANDs operators and nested conditions</li>
 * <li><code>$or</code> — ORs operators and nested conditions</li>
 * <li><code>$not</code> — negate all nested operators and conditions, is equivalent to NOT(conditionA AND conditionB)</li>
 * </ul>
 * Available operators:
 * <ul>
 * <li><code>$eq</code> — test for equality of the document field and the specified value</li>
 * <li><code>$neq</code> — test for not equality</li>
 * <li><code>$lt</code> — "less than", the document field is less than the specified value</li>
 * <li><code>$gt</code> — "greater than", the document field is larger than the specified value</li>
 * <li><code>$lte</code> — less or equal</li>
 * <li><code>$gte</code> — greater or equal</li>
 * <li><code>$isNull</code> — checks for null if the specified value is "true" or checks for not null if "false"</li>
 * <li><code>$date-from</code> — greater or equal for datetime fields</li>
 * <li><code>$date-to</code> — less or equal for datetime fields</li>
 * <li><code>$in</code> — checks for equality to any of the specified values in the array</li>
 * <li><code>$hasTag</code> — check the document field is JSON document contains the specified value as field name or value</li>
 * </ul>
 * </p>
 */

public class InMemoryDBSelectTask implements IDatabaseTask {

    private IFieldName collectionFieldName;
    private IFieldName criteriaFieldName;
    private IFieldName callbackFieldName;

    /**
     * Collection where to search the documents.
     */
    private String collection;
    /**
     * Criteria to search the document.
     */
    private IObject criteria;
    /**
     * Callback function to call when the documents are found.
     */
    private IAction<IObject[]> callback;

    /**
     * Creates the task.
     * @throws TaskPrepareException if cannot resolve IFieldName
     */
    public InMemoryDBSelectTask() throws TaskPrepareException {
        try {
            collectionFieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "collectionName");
            criteriaFieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "criteria");
            callbackFieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "callback");
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Failed to resolve \"IFieldName\"", e);
        }
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            collection = (String) query.getValue(collectionFieldName);
            criteria = (IObject) query.getValue(criteriaFieldName);
            callback = (IAction<IObject[]>) query.getValue(callbackFieldName);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new TaskPrepareException("Failed to getting values from query", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            IDatabase dataBase = IOC.resolve(Keys.getOrAdd(InMemoryDatabase.class.getCanonicalName()));
            List<IObject> result = dataBase.select(criteria, collection);
            callback.execute(result.toArray(new IObject[result.size()]));
        } catch (ResolutionException e) {
            throw new TaskExecutionException("Failed to resolve InMemoryDatabase", e);
        } catch (Exception e) {
            try {
                throw new TaskExecutionException("Select failed: criteria = " + criteria.serialize(), e);
            } catch (SerializeException | NullPointerException e1) {
                throw new TaskExecutionException("Select failed", e);
            }
        }
    }

}
