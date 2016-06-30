package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.wrapper.CriteriaCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.GetObjectFromCachedCollectionQuery;
import info.smart_tools.smartactors.core.cached_collection.wrapper.get_item.DateToMessage;
import info.smart_tools.smartactors.core.cached_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
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

import java.time.LocalDateTime;

/**
 * Task must search objects with target task
 */
public class GetObjectFromCachedCollectionTask implements IDatabaseTask {
    private IDatabaseTask getItemTask;

    /**
     * @param getItemTask Target task for getting items
     */
    public GetObjectFromCachedCollectionTask(final IDatabaseTask getItemTask) {
        this.getItemTask = getItemTask;
    }

    /**
     * Prepare
     * @param query query object
     *              <pre>
     *              {
     *                  "KEY_OF_COLLECTION" : "VALUE_FOR_KEY",
     *                  "collectionName" : "COLLECTION _NAME" //Not using but must be
     *              }    
     *              </pre>
     * @throws TaskPrepareException Throw when some was incorrect in preparing query
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        try {
            GetObjectFromCachedCollectionQuery srcQueryObject = IOC.resolve(
                    Keys.getOrAdd(
                            GetObjectFromCachedCollectionQuery.class.toString()
                    ),
                    query);

            CriteriaCachedCollectionQuery criteriaQuery =
                    IOC.resolve(
                            Keys.getOrAdd(
                                    CriteriaCachedCollectionQuery.class.toString()
                            ),
                            getResolvedIObject());

            EQMessage keyEQ = IOC.resolve(Keys.getOrAdd(EQMessage.class.toString()), getResolvedIObject());
            keyEQ.setEq(srcQueryObject.getKey());
            criteriaQuery.setKey(keyEQ);

            EQMessage isActiveEQ = IOC.resolve(Keys.getOrAdd(EQMessage.class.toString()), getResolvedIObject());
            isActiveEQ.setEq(Boolean.toString(true));
            criteriaQuery.setIsActive(isActiveEQ);

            DateToMessage startDateTimeDateTo = IOC.resolve(Keys.getOrAdd(DateToMessage.class.toString()), getResolvedIObject());
            startDateTimeDateTo.setDateTo(LocalDateTime.now().toString());
            criteriaQuery.setStartDateTime(startDateTimeDateTo);

            srcQueryObject.setPageNumber(0);
            srcQueryObject.setPageSize(100); // FIXME: 6/21/16 hardcode count must be fixed
            srcQueryObject.setCriteria(criteriaQuery);

            getItemTask.prepare(query);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create ISearchQuery from input query", e);
        } catch (ChangeValueException | ReadValueException e) {
            throw new TaskPrepareException("Can't change value in one of IObjects", e);
        }
    }

    /**
     * @param connection New connection for this and target tasks
     * @throws TaskSetConnectionException Throw when setting connection throw this exception
     */
    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        getItemTask.setConnection(connection);
    }

    /**
     * @throws TaskExecutionException Throw when target task can't execute query
     */
    @Override
    public void execute() throws TaskExecutionException {
        getItemTask.execute();
    }

    /**
     * @return new empty IObject
     * @throws ResolutionException Throw when IOC can't resolve IObject
     */
    private static IObject getResolvedIObject() throws ResolutionException {
        return IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.toString()));
    }
}
