package info.smart_tools.smartactors.core.examples.db_collection;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.base.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * A set of sample operations over database collection.
 */
public final class CollectionOperations {

    /**
     * Private constructor to avoid instantiation.
     */
    private CollectionOperations() {
    }

    /**
     * Creates the collection using "db.collection.create" task.
     * @param pool pool of connections
     * @param collection name of the collection
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is used incorrectly
     * @throws TaskExecutionException if the DB operation fails
     */
    public static void createCollection(final IPool pool, final CollectionName collection)
            throws ResolutionException, PoolGuardException, TaskExecutionException {
        IObject createOptions = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{ \"fulltext\": \"text\", \"language\": \"english\" }");
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.create"),
                    guard.getObject(),
                    collection,
                    createOptions
            );
            task.execute();
        }
        System.out.println("Created " + collection);
    }

    /**
     * Inserts new document to the collection using "db.collection.insert" task.
     * @param pool pool of connections
     * @param collection name of the collection
     * @param document document to insert
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws TaskExecutionException if the insertion failed
     * @throws SerializeException if the inserted document cannot be serialized
     */
    public static void insertDocument(final IPool pool, final CollectionName collection, final IObject document)
            throws ResolutionException, TaskExecutionException, SerializeException, PoolGuardException {
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.insert"),
                    guard.getObject(),
                    collection,
                    document
            );
            task.execute();
        }
        System.out.println("Inserted");
        System.out.println((String) document.serialize());
    }

    /**
     * Upserts the document in the collection using "db.collection.upsert" task.
     * @param pool pool of connections
     * @param collection name of the collection
     * @param document the document to upsert
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws TaskExecutionException if the upsert failed
     * @throws SerializeException if the upserted document cannot be serialized
     */
    public static void upsertDocument(final IPool pool, final CollectionName collection, final IObject document)
            throws ResolutionException, TaskExecutionException, SerializeException, PoolGuardException {
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.upsert"),
                    guard.getObject(),
                    collection,
                    document
            );
            task.execute();
        }
        System.out.println("Upserted");
        System.out.println((String) document.serialize());
    }

    /**
     * Gets the document by id using "db.collection.getbyid" task.
     * @param pool pool of connections
     * @param collection name of the collection
     * @param id the document id to get
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws TaskExecutionException if the selection failed
     */
    public static void getDocumentById(final IPool pool, final CollectionName collection, final Object id)
            throws ResolutionException, TaskExecutionException, PoolGuardException {
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.getbyid"),
                    guard.getObject(),
                    collection,
                    id,
                    (IAction<IObject>) doc -> {
                        try {
                            System.out.println("Found by id");
                            System.out.println((String) doc.serialize());
                        } catch (SerializeException e) {
                            throw new ActionExecutionException(e);
                        }
                    }
            );
            task.execute();
        }
    }

    /**
     * Searches the document by the int field using "db.collection.search" task.
     * @param pool pool of connections
     * @param collection collection name
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws InvalidArgumentException if search criteria is invalid
     * @throws TaskExecutionException if the selection failed
     */
    public static void searchDocumentByIntField(final IPool pool, final CollectionName collection)
            throws ResolutionException, TaskExecutionException, PoolGuardException, InvalidArgumentException {
        IFieldName intField = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "int");
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    guard.getObject(),
                    collection,
                    new DSObject(String.format(
                            "{ \"filter\": { \"%1$s\": { \"$eq\": 1 } } }",
                            intField.toString())),
                    (IAction<IObject[]>) docs -> {
                        try {
                            for (IObject doc : docs) {
                                System.out.println("Found by " + intField);
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
     * Search the document by the text field using "db.collection.search" task.
     * @param pool pool of connections
     * @param collection collection name
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws InvalidArgumentException if search criteria is invalid
     * @throws TaskExecutionException if the selection failed
     */
    public static void searchDocumentByTextField(final IPool pool, final CollectionName collection)
            throws ResolutionException, InvalidArgumentException, TaskExecutionException, PoolGuardException {
        IFieldName textField = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "text");
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    guard.getObject(),
                    collection,
                    new DSObject(String.format(
                            "{ " +
                                    "\"filter\": { \"%1$s\": { \"$fulltext\": \"update\" } }," +
                                    "\"page\": { \"size\": 2, \"number\": 1 }," +
                                    "\"sort\": [ { \"%1$s\": \"asc\" } ]" +
                                    "}",
                            textField.toString())),
                    (IAction<IObject[]>) docs -> {
                        try {
                            for (IObject doc : docs) {
                                System.out.println("Found by " + textField);
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
     * Search the document by the non-existing field using "db.collection.search" task.
     * @param pool pool of connections
     * @param collection collection name
     * @throws ResolutionException if not possible to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws InvalidArgumentException if search criteria is invalid
     * @throws TaskExecutionException if the selection failed
     */
    public static void searchDocumentByNoneField(final IPool pool, final CollectionName collection)
            throws ResolutionException, InvalidArgumentException, TaskExecutionException, PoolGuardException {
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    guard.getObject(),
                    collection,
                    new DSObject("{ " +
                                    "\"filter\": { \"none\": { \"$eq\": \"something\" } }" +
                                 "}"),
                    (IAction<IObject[]>) docs -> {
                        try {
                            for (IObject doc : docs) {
                                System.out.println("Found by none");
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
     * Deletes the document from the collection using "db.collection.delete" task.
     * @param pool pool of connections
     * @param collection collection name
     * @param document document to delete
     * @throws ResolutionException if failed to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws TaskExecutionException if failed to delete
     * @throws SerializeException if the document cannot be serialized
     */
    public static void deleteDocument(final IPool pool, final CollectionName collection, final IObject document)
            throws ResolutionException, TaskExecutionException, SerializeException, PoolGuardException {
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.delete"),
                    guard.getObject(),
                    collection,
                    document
            );
            task.execute();
        }
        System.out.println("Deleted");
        System.out.println((String) document.serialize());
    }

    /**
     * Counts documents in the collection by the int field using "db.collection.count" task.
     * @param pool pool of connections
     * @param collection collection name
     * @throws ResolutionException if failed to resolve something from IOC
     * @throws PoolGuardException if PoolGuard is misused
     * @throws InvalidArgumentException if search criteria is invalid
     * @throws TaskExecutionException if counting failed
     */
    public static void countByInt(final IPool pool, final CollectionName collection)
            throws PoolGuardException, ResolutionException, InvalidArgumentException, TaskExecutionException {
        IFieldName intField = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "int");
        try (PoolGuard guard = new PoolGuard(pool)) {
            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.count"),
                    guard.getObject(),
                    collection,
                    new DSObject(String.format(
                            "{ \"filter\": { \"%1$s\": { \"$eq\": 1 } } }",
                            intField.toString())),
                    (IAction<Long>) count -> {
                        System.out.println("Count by " + intField);
                        System.out.println(count);
                    }
            );
            task.execute();
        }
    }

}
