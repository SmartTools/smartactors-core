package info.smart_tools.smartactors.core.examples.db_collection;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.base.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.database_postgresql_plugins.postgres_connection_pool_plugin.PostgresConnectionPoolPlugin;
import info.smart_tools.smartactors.database_postgresql_plugins.postgres_db_tasks_plugin.PostgresDBTasksPlugin;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample server to test fulltext search in DB collection.
 */
public class TestPostgresSearchServer implements IServer {

    private IKey iObjectKey;
    private IKey iFieldKey;

    @Override
    public void initialize() throws ServerInitializeException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            new PluginIOCSimpleContainer(bootstrap).load();
            new PluginIOCKeys(bootstrap).load();
            new IFieldNamePlugin(bootstrap).load();
            new IFieldPlugin(bootstrap).load();
            new PluginDSObject(bootstrap).load();
            new PostgresConnectionPoolPlugin(bootstrap).load();
            new PostgresDBTasksPlugin(bootstrap).load();
            bootstrap.start();

            iObjectKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
            iFieldKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        } catch (Throwable e) {
            throw new ServerInitializeException("Server initialization failed", e);
        }
    }

    @Override
    public void start() throws ServerExecutionException {
        try {
            CollectionName collection = CollectionName.fromString("test_fulltext");

            IFieldName typeField = IOC.resolve(iFieldKey, "type");
            IFieldName brandField = IOC.resolve(iFieldKey, "brand");
            IFieldName modelField = IOC.resolve(iFieldKey, "model");

            IObject document = IOC.resolve(iObjectKey);
            document.setValue(typeField, "Планшет");
            document.setValue(brandField, "Lenovo");
            document.setValue(modelField, "FVVEE1224");

            ConnectionOptions connectionOptions = new TestConnectionOptions();
            IPool pool = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions);

            IFieldName fullTextField = IOC.resolve(iFieldKey, "fulltext");
            IFieldName languageField = IOC.resolve(iFieldKey, "language");
            IObject createOptions = IOC.resolve(iObjectKey);
            List<String> fulltext = new ArrayList<>();
            fulltext.add(typeField.toString());
            fulltext.add(brandField.toString());
            fulltext.add(modelField.toString());
            createOptions.setValue(fullTextField, fulltext);
            createOptions.setValue(languageField, "simple");

            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.create"),
                        guard.getObject(),
                        collection,
                        createOptions
                );
                task.execute();
            }

            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.insert"),
                        guard.getObject(),
                        collection,
                        document
                );
                task.execute();
            }

            IObject criteria;
            criteria = IOC.resolve(iObjectKey, "{" +
                    "\"filter\": { \"_\": { \"$fulltext\": \"Lenovo\" } }" +
                    "}");
            System.out.println("Found by " + criteria.serialize());
            search(pool, collection, criteria);

            criteria = IOC.resolve(iObjectKey, "{" +
                    "\"filter\": { \"_\": { \"$fulltext\": \"ovo\" } }" +
                    "}");
            System.out.println("Found by " + criteria.serialize());
            search(pool, collection, criteria);

            criteria = IOC.resolve(iObjectKey, "{" +
                    "\"filter\": { \"_\": { \"$fulltext\": \"lenovo планшет\" } }" +
                    "}");
            System.out.println("Found by " + criteria.serialize());
            search(pool, collection, criteria);

        } catch (Exception e) {
            throw new ServerExecutionException(e);
        }
    }

    private void search(final IPool pool, final CollectionName collection, final IObject criteria)
            throws ResolutionException, TaskExecutionException, PoolGuardException {
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    guard.getObject(),
                    collection,
                    criteria,
                    (IAction<IObject[]>) docs -> {
                        try {
                            for (IObject doc : docs) {
                                System.out.println((String) doc.serialize());
                            }
                        } catch (SerializeException e) {
                            throw new ActionExecutionException(e);
                        }
                    }
            );
            task.execute();
        }
    }

    /**
     * Runs the server
     * @param args ignored
     * @throws ServerInitializeException when the server initialization failed
     * @throws ServerExecutionException when the server execution failed
     */
    public static void main(final String[] args) throws ServerInitializeException, ServerExecutionException {
        IServer server = new TestPostgresSearchServer();
        server.initialize();
        server.start();
    }

}
