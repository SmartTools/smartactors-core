package info.smart_tools.smartactors.database_postgresql.postgres_count_task;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.JDBCCompiledQuery;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The database task which is able to count documents in Postgres database by multiple search criteria.
 * The search criteria are passed in the complex IObject to the {@link #prepare(IObject)} method.
 * <p>
 *     The example of prepare object:
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
 *       "callback": IAction callback to receive the number of found documents
 *   }
 * }
 * </p>
 * <p>
 *     The criteria is the IObject which contains a set of conditions and operators.
 *     Conditions joins operators together.
 *     Operators match the specified document field against the specified criteria.
 * </p>
 *
 *     Available conditions:
 *     <ul>
 *         <li><code>$and</code> — ANDs operators and nested conditions</li>
 *         <li><code>$or</code> — ORs operators and nested conditions</li>
 *         <li><code>$not</code> — negate all nested operators and conditions, is equivalent to NOT(conditionA AND conditionB)</li>
 *     </ul>
 *     Available operators:
 *     <ul>
 *         <li><code>$eq</code> — test for equality of the document field and the specified value</li>
 *         <li><code>$neq</code> — test for not equality</li>
 *         <li><code>$lt</code> — "less than", the document field is less than the specified value</li>
 *         <li><code>$gt</code> — "greater than", the document field is larger than the specified value</li>
 *         <li><code>$lte</code> — less or equal</li>
 *         <li><code>$gte</code> — greater or equal</li>
 *         <li><code>$isNull</code> — checks for null if the specified value is "true" or checks for not null if "false"</li>
 *         <li><code>$date-from</code> — greater or equal for datetime fields</li>
 *         <li><code>$date-to</code> — less or equal for datetime fields</li>
 *         <li><code>$in</code> — checks for equality to any of the specified values in the array</li>
 *         <li><code>$hasTag</code> — check the document field is JSON document contains the specified value as field name or value</li>
 *     </ul>
 *
 */
public class PostgresCountTask implements IDatabaseTask {

    /**
     * Connection to the database.
     */
    private IStorageConnection connection;
    /**
     * Name of the collection.
     */
    private CollectionName collection;
    /**
     * Criteria to count documents.
     */
    private IObject criteria;
    /**
     * Callback function to call when the documents are counted.
     */
    private IAction<Long> callback;
    /**
     * Query, prepared during prepare(), to be compiled during execute().
     */
    private QueryStatement preparedQuery;

    /**
     * Creates the task
     * @param connection the database connection where to perform counts
     */
    public PostgresCountTask(final IStorageConnection connection) {
        this.connection = connection;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            CountMessage message = IOC.resolve(Keys.getKeyByName(CountMessage.class.getCanonicalName()), query);
            collection = message.getCollectionName();
            criteria = message.getCriteria();
            callback = message.getCallback();

            preparedQuery = new QueryStatement();
            PostgresSchema.count(preparedQuery, collection, criteria);
        } catch (Exception e) {
            throw new TaskPrepareException(e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            JDBCCompiledQuery compiledQuery = (JDBCCompiledQuery) connection.compileQuery(preparedQuery);
            PreparedStatement statement = compiledQuery.getPreparedStatement();
            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                long result = resultSet.getLong(1);
                connection.commit();
                callback.execute(result);
            } else {
                connection.commit();
                throw new TaskExecutionException("Failed to get result of counting");
            }
        } catch (TaskExecutionException tee) {
            throw tee;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            try {
                throw new TaskExecutionException("Count in " + collection + " failed: criteria = " +
                        (criteria != null ? criteria.serialize() : "null"), e);
            } catch (SerializeException se) {
                throw new TaskExecutionException("Count in " + collection + " failed", e);
            }
        }
    }

}
