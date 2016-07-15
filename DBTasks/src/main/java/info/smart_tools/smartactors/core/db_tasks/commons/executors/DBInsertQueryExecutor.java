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
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Common executor for insert query to database.
 * Not dependent of database type.
 * All insert tasks may use this executor.
 * @see IDBQueryExecutor
 */
public final class DBInsertQueryExecutor implements IDBQueryExecutor {

    private DBInsertQueryExecutor() {}

    /**
     * Factory-method for creation a new instance of {@link DBInsertQueryExecutor}.
     * @return a new instance of {@link DBInsertQueryExecutor}.
     */
    public static DBInsertQueryExecutor create() {
        return new DBInsertQueryExecutor();
    }

    /**
     * Checks the insert query on executable.
     * @see IDBQueryExecutor#isExecutable(IObject)
     *
     * @param message - query message with a some parameters for query.
     * @return <code>true</code> if message contains a document, else false.
     * @exception InvalidArgumentException when incoming message has invalid format.
     */
    @Override
    public boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        try {
            return DBQueryFields.DOCUMENT.in(message) != null;
        } catch (ReadValueException e) {
            throw new InvalidArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Executes the insert query.
     * One call = one insert.
     * @see IDBQueryExecutor#execute(ICompiledQuery, IObject)
     *
     * After executes adds in source object generated id in database.
     *
     * @param query - prepared compiled query for execution.
     * @param message - query message with parameters for query.
     * @exception TaskExecutionException when database return not enough generated ids
     *              or errors in during execution create collection query to database.
     */
    @Override
    public void execute(@Nonnull final ICompiledQuery query, @Nonnull final IObject message)
            throws TaskExecutionException {
        try {
            ResultSet resultSet = query.executeQuery();
            addIdsInResult(resultSet, message);
        } catch (QueryExecutionException e) {
            throw new TaskExecutionException("'Insert query' execution has been failed: " + e.getMessage(), e);
        }
    }

    private void addIdsInResult(
            final ResultSet resultSet,
            final IObject message
    ) throws TaskExecutionException {
        try {
            int docCounter = 0;
            while (resultSet.next()) {
                try {
                    ICollectionName collection = DBQueryFields.COLLECTION.in(message);
                    setDocumentId(DBQueryFields.DOCUMENT.in(message), collection.toString(), resultSet.getLong("id"));
                } catch (ChangeValueException e) {
                    throw new TaskExecutionException("Could not set new id on inserted document.");
                } catch (ReadValueException | InvalidArgumentException e) {
                    throw new TaskExecutionException("Could not read document with index " + docCounter + ".");
                } catch (NullPointerException e) {
                    throw new TaskExecutionException("Invalid qeury message!");
                }

                docCounter++;
            }
            if (docCounter > 1) {
                throw new TaskExecutionException("Database returned too much of generated ids.");
            }
            if (docCounter < 1) {
                throw new TaskExecutionException("Database returned not enough of generated ids.");
            }
        } catch (SQLException | ResolutionException e) {
            throw new TaskExecutionException(e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new TaskExecutionException("Database didn't returned a result set.");
        }
    }

    private void setDocumentId(final IObject document, final String collection, final Long documentId)
            throws ChangeValueException, ResolutionException, InvalidArgumentException {
        IField id = IDContainer.getIdFieldFor(collection);
        id.out(document, documentId);
    }
}
