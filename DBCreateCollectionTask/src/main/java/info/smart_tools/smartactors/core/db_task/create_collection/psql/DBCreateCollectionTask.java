package info.smart_tools.smartactors.core.db_task.create_collection.psql;

import info.smart_tools.smartactors.core.db_storage.DataBaseStorage;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.PreparedQuery;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_storage.utils.ConnectionPool;
import info.smart_tools.smartactors.core.db_task.create_collection.psql.wrapper.CreateCollectionQuery;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Task for create collection with predefined indexes
 */
public class DBCreateCollectionTask implements IDatabaseTask {

    private CompiledQuery compiledQuery;
    private ConnectionPool connectionPool;

    private static Map<String,String> indexCreationTemplates = new HashMap<String,String>() {{
        put("ordered","CREATE INDEX ON %s USING BTREE ((%s));\n");
        put("tags","CREATE INDEX ON %s USING GIN ((%s));\n");
        put("fulltext",String.format("CREATE INDEX ON %%s USING GIN ((to_tsvector('%s',(%%s)::text)));\n",Schema.FTS_DICTIONARY));
        put("datetime","CREATE INDEX ON %s USING BTREE ((parse_timestamp_immutable(%s)));\n");
        put("id","CREATE INDEX ON %1$s USING BTREE ((%2$s));\nCREATE INDEX ON %1$s USING HASH ((%2$s));\n");
    }};

    public DBCreateCollectionTask(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void prepare(final IObject createCollectionMessage) throws TaskPrepareException {

        try {
            CreateCollectionQuery message = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), CreateCollectionQuery.class.toString()), createCollectionMessage);
            QueryStatement preparedQuery = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), PreparedQuery.class.toString()));
            CollectionName collectionName = CollectionName.fromString(message.getCollectionName());

            Writer writer = preparedQuery.getBodyWriter();
            writer.write(String.format(
                "CREATE TABLE %s (%s %s PRIMARY KEY, %s JSONB NOT NULL);\n",
                collectionName.toString(),
                Schema.ID_COLUMN_NAME ,Schema.ID_COLUMN_SQL_TYPE, Schema.DOCUMENT_COLUMN_NAME)
            );
            if (!message.getIndexes().containsKey("id")) {
                message.getIndexes().put("id","id");
            }
            for (Map.Entry<String, String> entry : message.getIndexes().entrySet()) {
                FieldPath field = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), FieldPath.class.toString()), entry.getKey());
                String indexType = entry.getValue();
                String tpl = indexCreationTemplates.get(indexType);

                if (tpl == null) {
                    throw new TaskPrepareException("Invalid index type: " + indexType);
                }

                preparedQuery.getBodyWriter().write(String.format(tpl, collectionName.toString(), field.getSQLRepresentation()));
            }
            this.compiledQuery = connectionPool.getConnection().compileQuery(preparedQuery);
        } catch (ReadValueException | ChangeValueException| ResolutionException | StorageException | IOException e) {
            throw new TaskPrepareException("Error while writing collection creation statement.",e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {

        try {
            DataBaseStorage.executeTransaction(connectionPool, (connection) -> {
                try {
                    ((JDBCCompiledQuery)compiledQuery).getPreparedStatement().execute();
                } catch (Exception e) {
                    throw new StorageException("Collection creation query execution failed because of SQL exception.",e);
                }
            });
        } catch (Exception e) {
            throw new TaskExecutionException("Transaction execution has been failed.", e);
        }
    }
}
