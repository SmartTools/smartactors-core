package info.smart_tools.smartactors.core.db_tasks.commons.executors;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.db_tasks.utils.IDContainer;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common executor for search query to database.
 * Not dependent of database type.
 * All search tasks may use this executor.
 * @see IDBQueryExecutor
 */
public final class DBSearchQueryExecutor implements IDBQueryExecutor {

    private DBSearchQueryExecutor() {}

    /**
     * Factory-method for creation a new instance of {@link DBSearchQueryExecutor}.
     * @return a new instance of {@link DBSearchQueryExecutor}.
     */
    public static DBSearchQueryExecutor create() {
        return new DBSearchQueryExecutor();
    }

    /**
     * Checks the search query on executable.
     * Always gives <code>true</code> because this method must be override a impl. classes.
     * @see IDBQueryExecutor#isExecutable(IObject)
     *
     * @param message - query message with a some parameters for query.
     * @return always <code>true</code>.
     * @exception InvalidArgumentException never throws in current executor.
     */
    @Override
    public boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        return true;
    }

    /**
     * Executes the search query.
     * @see IDBQueryExecutor#execute(ICompiledQuery, IObject)
     *
     * @param query - prepared compiled query for execution.
     * @param message - query message with parameters for query.
     * @exception TaskExecutionException when:
     *              1. Errors of resolution a IObject or IFieldName dependencies;
     *              2. Errors of setting a document's id field in result object
     *                      or search result field in the incoming message.
     *              3. The incoming message has a invalid format.
     *              4. Errors in during execution create collection query to database.
     */
    @Override
    public void execute(@Nonnull final ICompiledQuery query, @Nonnull final IObject message)
            throws TaskExecutionException {
        try {
            ICollectionName collectionName = DBQueryFields.COLLECTION.in(message);
            ResultSet resultSet = query.executeQuery();
            List<IObject> resultObjects = new ArrayList<>();
            while (resultSet.next()) {
                String jsonValue = resultSet.getString("document");
                IObject object;
                try {
                    object = IOC.resolve(Keys.getOrAdd(IObject.class.toString()), jsonValue);
                    IField idFN = IDContainer.getIdFieldFor(collectionName.toString());
                    idFN.out(object, resultSet.getLong("id"));
                } catch (ChangeValueException e) {
                    throw new TaskExecutionException("Could not set document's id field.", e);
                } catch (ResolutionException e) {
                    throw new TaskExecutionException(e.getMessage(), e);
                } catch (InvalidArgumentException e) {
                    throw new TaskExecutionException("Invalid argument exception", e);
                }

                resultObjects.add(object);
            }

            DBQueryFields.SEARCH_RESULT.out(message, resultObjects);
        } catch (QueryExecutionException | SQLException e) {
            throw new TaskExecutionException("'Search query' execution has been failed: " + e.getMessage(), e);
        } catch (InvalidArgumentException | ChangeValueException e) {
            throw new TaskExecutionException("Error writing search result: " + e.getMessage(), e);
        } catch (ReadValueException e) {
            throw new TaskExecutionException("Error reading collection name from message: " + e.getMessage(), e);
        }
    }
}
