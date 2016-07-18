package info.smart_tools.smartactors.core.db_task.upsert.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.upsert.psql.wrapper.UpsertMessage;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import info.smart_tools.smartactors.core.sql_commons.psql.Schema;

import java.io.IOException;
import java.io.Writer;

//TODO:: remove this stub!
public class DBInsertTask implements IDatabaseTask {

    private CompiledQuery compiledQuery;
    private QueryStatement queryStatement;
    private StorageConnection connection;

    public DBInsertTask() {
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        //TODO:: replace by InsertMessage iobject wrapper
        try {
            UpsertMessage upsertMessage = IOC.resolve(Keys.getOrAdd(UpsertMessage.class.getCanonicalName()), query);
            String collectionName = upsertMessage.getCollectionName();
            Writer writer = queryStatement.getBodyWriter();
            writer.write(String.format(
                "INSERT INTO %s (%s) VALUES", CollectionName.fromString(collectionName).toString(), Schema.DOCUMENT_COLUMN_NAME
            ));
            writer.write("(?::jsonb)");
            writer.write(String.format(" RETURNING %s AS id;", Schema.ID_COLUMN_NAME));

            this.compiledQuery = connection.compileQuery(queryStatement);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Error while resolving insert query statement.", e);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Error while get collection name.", e);
        } catch (IOException | QueryBuildException e) {
            throw new TaskPrepareException("Error while initialize insert query.", e);
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        this.connection = connection;
    }

    @Override
    public void execute() throws TaskExecutionException {

    }

    public CompiledQuery getCompiledQuery() {
        return compiledQuery;
    }
}
