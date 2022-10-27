package info.smart_tools.smartactors.scheduler.actor.impl.remote_storage;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.ipool_guard.IPoolGuard;
import info.smart_tools.smartactors.base.interfaces.ipool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.base.pool_guard.PoolGuard;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryNotFoundException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.util.Arrays;
import java.util.List;

/**
 * Remote entry storage storing entries in database.
 *
 * <p>
 * Collection this storage works with should support 64-bit integers as numeric values.
 * </p>
 */
public class DatabaseRemoteStorage implements IRemoteEntryStorage {
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Name of entry state field used only by {@link DatabaseRemoteStorage} to store last time the entry was scheduled on.
     *
     * To minimize risk of collisions this filed name contains some unpredictable underscores and abbreviations so it's even a bit more
     * difficult to collide with than my password.
     */
    private static final String LAST_SCHEDULED_TIME_FIELD_NAME = "__last_sched_time_";

    private final IPool connectionPool;
    private final String collectionName;

    private final IFieldName filterFieldName;
    private final IFieldName gtFieldName;
    private final IFieldName ltFieldName;
    private final IFieldName eqFieldName;
    private final IFieldName entryIdFieldName;
    private final IFieldName pageFieldName;
    private final IFieldName sizeFieldName;
    private final IFieldName documentIdFieldName;
    private final IFieldName lastScheduledTimeFieldName;

    private final IObject entriesQuery;
    private List<IObject> entriesQueryResult;

    /**
     * The constructor.
     *
     * @param connectionPool    database connection pool
     * @param collectionName    name of database collection to use
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public DatabaseRemoteStorage(final IPool connectionPool, final String collectionName)
            throws ResolutionException {
        this.connectionPool = connectionPool;
        this.collectionName = collectionName;

        filterFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "filter");
        gtFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "$gt");
        ltFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "$lt");
        eqFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "$eq");
        entryIdFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "entryId");
        pageFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "page");
        sizeFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "size");
        documentIdFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), collectionName.toLowerCase() + "ID");
        lastScheduledTimeFieldName = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), LAST_SCHEDULED_TIME_FIELD_NAME);

        entriesQuery = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                String.format(("{" +
                        "'filter':{'" + LAST_SCHEDULED_TIME_FIELD_NAME + "':{}}," +
                        "'page':{'size':%s,'number':1}," +
                        "'sort':[{'" + LAST_SCHEDULED_TIME_FIELD_NAME + "':'asc'}]" +
                        "}").replace('\'', '"'), DEFAULT_PAGE_SIZE));
    }

    @Override
    public void saveEntry(final ISchedulerEntry entry) throws EntryStorageAccessException {
        try (IPoolGuard guard = new PoolGuard(connectionPool)) {
            entry.getState().setValue(lastScheduledTimeFieldName, entry.getLastTime());

            Object connection = guard.getObject();

            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.upsert"),
                    connection,
                    collectionName,
                    entry.getState());

            task.execute();
        } catch (ResolutionException | PoolGuardException | TaskExecutionException | ChangeValueException | InvalidArgumentException e) {
            throw new EntryStorageAccessException("Error occurred saving scheduler entry to database.", e);
        }
    }

    @Override
    public void deleteEntry(final ISchedulerEntry entry) throws EntryStorageAccessException {
        try (IPoolGuard guard = new PoolGuard(connectionPool)) {

            Object connection = guard.getObject();

            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.delete"),
                    connection,
                    collectionName,
                    entry.getState());

            task.execute();
        } catch (PoolGuardException | ResolutionException | TaskExecutionException e) {
            throw new EntryStorageAccessException("Error occurred deleting entry from database.", e);
        }
    }

    @Override
    public IObject querySingleEntry(final String id) throws EntryStorageAccessException, EntryNotFoundException {
        try (IPoolGuard guard = new PoolGuard(connectionPool)) {
            final IObject[] res = new IObject[1];
            Object connection = guard.getObject();

            // {"filter":{"entryId":{"$eq":id}}}
            IObject query = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            IObject filter = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            IObject entryIdFilter = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            query.setValue(filterFieldName, filter);
            filter.setValue(entryIdFieldName, entryIdFilter);
            entryIdFilter.setValue(eqFieldName, id);

            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    connection,
                    collectionName,
                    query,
                    (IAction<IObject[]>) docs -> {
                        if (docs.length != 0) {
                            res[0] = docs[0];
                        }
                    });

            task.execute();

            if (null == res[0]) {
                throw new EntryNotFoundException("No entry with id='" + id + "' found in remote storage.");
            }

            return res[0];
        } catch (PoolGuardException | ResolutionException | ChangeValueException | InvalidArgumentException | TaskExecutionException e) {
            throw new EntryStorageAccessException("Error occurred downloading saved scheduler entry state.", e);
        }
    }

    @Override
    public List<IObject> downloadEntries(final long untilTime, final IObject lastSkip, final int pageSize) throws EntryStorageAccessException {

        try (IPoolGuard guard = new PoolGuard(connectionPool)) {
            Object connection = guard.getObject();

            IObject page = (IObject) entriesQuery.getValue(pageFieldName);
            page.setValue(sizeFieldName, pageSize);

            // This method will be called from at most one thread at time so it should be safe to re-use the query object and result field
            IObject filter = (IObject) entriesQuery.getValue(filterFieldName);
            IObject filterSchTime = (IObject) filter.getValue(lastScheduledTimeFieldName);

            if (lastSkip != null) {
                filterSchTime.setValue(gtFieldName, lastSkip.getValue(lastScheduledTimeFieldName));
            } else {
                filterSchTime.deleteField(gtFieldName);
            }

            filterSchTime.setValue(ltFieldName, untilTime);

            ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.search"),
                    connection,
                    collectionName,
                    entriesQuery,
                    (IAction<IObject[]>) docs -> {
                        try {
                            entriesQueryResult = Arrays.asList(docs);
                        } catch (Exception e) {
                            throw new ActionExecutionException(e);
                        }
                    }
            );

            task.execute();

            return entriesQueryResult;
        } catch (PoolGuardException | ResolutionException | TaskExecutionException | ReadValueException | ChangeValueException
                | InvalidArgumentException | DeleteValueException e) {
            throw new EntryStorageAccessException("Error occurred downloading page of scheduler entries.", e);
        }
    }

    @Override
    public void weakSaveEntry(final ISchedulerEntry entry) throws EntryStorageAccessException {
        try {
            if (null == entry.getState().getValue(documentIdFieldName)) {
                return;
            }

            Number savedScheduledTime = (Number) entry.getState().getValue(lastScheduledTimeFieldName);

            if (savedScheduledTime != null && savedScheduledTime.longValue() == entry.getLastTime()) {
                return;
            }

            saveEntry(entry);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new EntryStorageAccessException("", e);
        } catch (EntryStorageAccessException ignore) { }
    }
}
