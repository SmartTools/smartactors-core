package info.smart_tools.smartactors.database_postgresql.postgres_search_by_page_size_and_number_task;

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
import java.util.ArrayList;
import java.util.List;

/**
 * The database task which is able to select documents from Postgres database by multiple search criteria.
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
 *           },
 *           "page": {
 *               "size": 50,
 *               "number": 2
 *           },
 *           "sort": [
 *               { "a": "asc" },
 *               { "b": "desc" }
 *           ]
 *       },
 *       "callback": IAction callback to receive found documents
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
public class PostgresSearchByPageSizeAndNumberTask implements IDatabaseTask {

    /**
     * Connection to the database.
     */
    private IStorageConnection connection;
    /**
     * Name of the collection.
     */
    private CollectionName collection;
    /**
     * Criteria to search the document.
     */
    private IObject criteria;
    /**
     * Callback function to call when the object is found.
     */
    private IAction<IObject[]> callback;
    /**
     * Query, prepared during prepare(), to be compiled during execute().
     */
    private QueryStatement preparedQuery;

    /**
     * Creates the task
     * @param connection the database connection where to perform search
     */
    public PostgresSearchByPageSizeAndNumberTask(final IStorageConnection connection) {
        this.connection = connection;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            SearchByPageSizeAndNumberMessage message = IOC.resolve(Keys.getKeyByName(SearchByPageSizeAndNumberMessage.class.getCanonicalName()), query);
            collection = message.getCollectionName();
            criteria = message.getCriteria();
            callback = message.getCallback();

            preparedQuery = new QueryStatement();
            PostgresSchema.searchByPageSizeAndNumber(preparedQuery, collection, criteria);
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
            List<IObject> results = new ArrayList<>();
            while (resultSet.next()) {
                String sqlDoc = resultSet.getString(1);
                IObject document = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), sqlDoc);
                results.add(document);
            }

            connection.commit();
            callback.execute(results.toArray(new IObject[results.size()]));
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception re) {
                // ignoring rollback failure
            }
            try {
                throw new TaskExecutionException("Select in " + collection + " failed: criteria = " +
                        (criteria != null ? criteria.serialize() : "null"), e);
            } catch (SerializeException se) {
                throw new TaskExecutionException("Select in " + collection + " failed", e);
            }
        }
    }

}
