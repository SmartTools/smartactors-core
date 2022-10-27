package info.smart_tools.smartactors.actors;

import info.smart_tools.smartactors.actors.exception.SampleDBException;
import info.smart_tools.smartactors.actors.wrapper.SampleGetByIdWrapper;
import info.smart_tools.smartactors.actors.wrapper.SampleUpsertWrapper;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Sample actor which upserts and retreives the document from database.
 * NOTE: IT'S NOT COMMON TO PUT ALL DB OPERATIONS INTO SINGLE ACTOR, HERE IT'S DONE ONLY FOR EXAMPLE.
 * Also it's not common to actor to access any collection, typically the actor works only with one collection.
 */
public class SampleDBActor {

    /**
     * Pool of DB connections.
     */
    private final IPool pool;

    /**
     * Constructs the actor. Resolves connection pool here.
     */
    public SampleDBActor() throws SampleDBException {
        try {
            ConnectionOptions options = IOC.resolve(Keys.getKeyByName("PostgresConnectionOptions"));
            pool = IOC.resolve(Keys.getKeyByName("PostgresConnectionPool"), options);
        } catch (ResolutionException e) {
            throw new SampleDBException("Cannot create actor", e);
        }
    }

    public void upsertDocument(SampleUpsertWrapper wrapper) throws SampleDBException {
        String collectionName = null;
        IObject document = null;
        try {
            collectionName = wrapper.getCollectionName();
            document = wrapper.getDocument();

            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.upsert"),
                        guard.getObject(),
                        collectionName,
                        document
                );
                task.execute();
            }

            wrapper.setDocument(document);
        } catch (Exception e) {
            try {
                throw new SampleDBException("Failed to upsert document " + document.serialize() + " into " + collectionName, e);
            } catch (SerializeException e1) {
                throw new SampleDBException("Failed to upsert unserializable document into " + collectionName, e);
            }
        }
    }

    public void getDocumentById(SampleGetByIdWrapper wrapper)
            throws SampleDBException {
        String collectionName = null;
        Object id = null;
        try {
            collectionName = wrapper.getCollectionName();
            id = wrapper.getDocumentId();

            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.getbyid"),
                        guard.getObject(),
                        wrapper.getCollectionName(),
                        id,
                        (IAction<IObject>) doc -> {
                            try {
                                wrapper.setDocument(doc);
                            } catch (ChangeValueException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
                task.execute();
            }
        } catch (Exception e) {
            throw new SampleDBException("Failed to get document " + id + " in " + collectionName, e);
        }
    }
}
