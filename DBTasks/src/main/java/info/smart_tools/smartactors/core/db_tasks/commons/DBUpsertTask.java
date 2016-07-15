package info.smart_tools.smartactors.core.db_tasks.commons;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ICollectionName;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.db_tasks.utils.IDContainer;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class DBUpsertTask implements IDatabaseTask {
    /**  */
    private Map<Boolean, IDatabaseTask> subTasks;
    /**  */
    private IDatabaseTask currentTask;
    /**  */
    private IStorageConnection connection;

    /**  */
    private static final boolean INSERT_MODE = true;
    /**  */
    private static final boolean UPDATE_MODE = false;

    /**
     *
     */
    protected DBUpsertTask() {
        subTasks = new HashMap<>(2);
    }

    /**
     *
     * @param upsertMessage
     * @throws TaskPrepareException
     */
    @Override
    public void prepare(final IObject upsertMessage) throws TaskPrepareException {
        try {
            ICollectionName collection = DBQueryFields.COLLECTION.in(upsertMessage);
            IObject document = DBQueryFields.DOCUMENT.in(upsertMessage);
            prepareDocuments(collection.toString(), document);
            prepareSubTask(currentTask, upsertMessage);
        } catch (NullPointerException e) {
            throw new TaskPrepareException("Invalid query message!", e);
        } catch (Throwable e) {
            throw new TaskPrepareException("Can't prepare upsert task because: " + e.getMessage(), e);
        }
    }

    /**
     *
     * @throws TaskExecutionException
     */
    @Override
    public void execute() throws TaskExecutionException {
        try {
            currentTask.execute();
            currentTask = null;
        } catch (NullPointerException e) {
            throw new TaskExecutionException("Prepare task before execution!");
        } catch (Throwable e) {
            currentTask = null;
            throw new TaskExecutionException("'Upsert task' execution has been failed because:" + e.getMessage(), e);
        }
    }

    /**
     *
     * @param storageConnection - database connection.
     */
    @Override
    public void setConnection(final IStorageConnection storageConnection) {
        connection = storageConnection;
    }

    /**
     *
      * @return
     */
    @Override
    public IStorageConnection getConnection() {
        return connection;
    }

    /**
     *
     * @param insertTask
     * @return
     */
    protected DBUpsertTask setInsertTask(@Nonnull final IDatabaseTask insertTask) {
        subTasks.put(INSERT_MODE, insertTask);
        return this;
    }

    /**
     *
     * @param updatetTask
     * @return
     */
    protected DBUpsertTask setUpdatetTask(@Nonnull final IDatabaseTask updatetTask) {
        subTasks.put(UPDATE_MODE, updatetTask);
        return this;
    }

    private void prepareDocuments(final String collection, final IObject document)
            throws ResolutionException, ReadValueException, InvalidArgumentException {
        IField idF = IDContainer.getIdFieldFor(collection);
        Object id = idF.in(document);
        currentTask = subTasks.get(id == null);
    }

    private void prepareSubTask(final IDatabaseTask task, final IObject message)
            throws ChangeValueException, TaskPrepareException, ResolutionException {
        task.setConnection(connection);
        task.prepare(message);
    }
}
