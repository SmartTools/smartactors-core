package info.smart_tools.smartactors.core.db_task.create_collection.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.create_collection.psql.wrapper.CreateCollectionQuery;
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
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import info.smart_tools.smartactors.core.sql_commons.QueryStatementFactory;
import info.smart_tools.smartactors.core.sql_commons.exception.QueryStatementFactoryException;
import info.smart_tools.smartactors.core.sql_commons.psql.Schema;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Task for create collection with predefined indexes
 */
public class DBCreateCollectionTask implements IDatabaseTask {

    private CompiledQuery compiledQuery;
    private StorageConnection connection;

    private static Map<String, String> indexCreationTemplates = new HashMap<String, String>() {{
        put("ordered", "CREATE INDEX ON %s USING BTREE ((%s));\n");
        put("tags", "CREATE INDEX ON %s USING GIN ((%s));\n");
        put("fulltext", String.format("CREATE INDEX ON %%s USING GIN ((to_tsvector('%s',(%%s)::text)));\n", Schema.FTS_DICTIONARY));
        put("datetime", "CREATE INDEX ON %s USING BTREE ((parse_timestamp_immutable(%s)));\n");
        put("id", "CREATE INDEX ON %1$s USING BTREE ((%2$s));\nCREATE INDEX ON %1$s USING HASH ((%2$s));\n");
    }};

    public DBCreateCollectionTask() {}

    @Override
    public void prepare(final IObject createCollectionMessage) throws TaskPrepareException {

        try {
            QueryStatementFactory factory = () -> {
                QueryStatement preparedQuery = new QueryStatement();
                Writer writer = preparedQuery.getBodyWriter();
                try {
                    CreateCollectionQuery message = IOC.resolve(
                            Keys.getOrAdd(CreateCollectionQuery.class.getCanonicalName()),
                            createCollectionMessage
                    );
                    CollectionName collectionName = CollectionName.fromString(message.getCollectionName());
                    writer.write(String.format(
                        "CREATE TABLE %s (%s %s PRIMARY KEY, %s JSONB NOT NULL);\n",
                        collectionName.toString(),
                        Schema.ID_COLUMN_NAME , Schema.ID_COLUMN_SQL_TYPE, Schema.DOCUMENT_COLUMN_NAME)
                    );
                    if (!message.getIndexes().containsKey("id")) {
                        message.getIndexes().put("id", "id");
                    }
                    for (Map.Entry<String, String> entry : message.getIndexes().entrySet()) {
                        FieldPath field = IOC.resolve(Keys.getOrAdd(FieldPath.class.getCanonicalName()), entry.getKey());
                        String indexType = entry.getValue();
                        String tpl = indexCreationTemplates.get(indexType);

                        if (tpl == null) {
                            throw new QueryStatementFactoryException("Invalid index type: " + indexType);
                        }
                        preparedQuery.getBodyWriter().write(
                                String.format(tpl, collectionName.toString(), field.getSQLRepresentation())
                        );
                    }
                } catch (
                        IOException |
                        QueryBuildException |
                        ResolutionException |
                        ChangeValueException |
                        ReadValueException e
                ) {
                    throw new QueryStatementFactoryException("Error while initialize update query.", e);
                }
                return preparedQuery;
            };

            this.compiledQuery = IOC.resolve(
                    Keys.getOrAdd(CompiledQuery.class.getCanonicalName()),
                    connection,
                    //TODO: please, check this (was toString())
                    DBCreateCollectionTask.class.getCanonicalName(),
                    factory
            );
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Error while writing collection creation statement.", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {

        try {
            ((JDBCCompiledQuery) compiledQuery).getPreparedStatement().execute();
        } catch (Exception e) {
            throw new TaskExecutionException("Collection creation query execution failed because of SQL exception.", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        this.connection = connection;
    }
}
