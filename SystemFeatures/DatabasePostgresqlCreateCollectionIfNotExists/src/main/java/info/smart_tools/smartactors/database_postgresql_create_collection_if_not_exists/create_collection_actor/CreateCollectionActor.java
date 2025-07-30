package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor;

import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.ipool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.exception.CreateCollectionActorException;
import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.wrapper.CreateCollectionWrapper;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * This actor gets a {@link ConnectionOptions} object from the IOC by the key from this actor's wrapper.
 * Then it gets an {@link IPool} object from the IOC by hard-coded name `PostgresConnectionPool`.
 * Then using collection name field from the wrapper it calls `db.collection.create-if-not-exists` {@link ITask}
 * so creates collection if it is not exist yet.
 */
public class CreateCollectionActor {
    public void createTable(CreateCollectionWrapper message) throws CreateCollectionActorException {
        try {
            final ConnectionOptions options = IOC.resolve(Keys.getKeyByName(message.getConnectionOptionsRegistrationName()));
            final IPool pool = IOC.resolve(Keys.getKeyByName("PostgresConnectionPool"), options);

            String collectionName = message.getCollectionName();
            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getKeyByName("db.collection.create-if-not-exists"),
                        guard.getObject(),
                        collectionName,
                        message.getOptions()
                );
                task.execute();
            } catch (TaskExecutionException | ResolutionException | PoolGuardException e) {
                throw new CreateCollectionActorException(e);
            }
        } catch (ReadValueException | ResolutionException e) {
            throw new CreateCollectionActorException(e);
        }
    }
}
