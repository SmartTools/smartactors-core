package info.smart_tools.smartactors.database.interfaces.idatabase_task;


import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * Database oriented task
 * @see ITask
 */
public interface IDatabaseTask extends ITask {

    /**
     * Prepares database query
     * @param query query object
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    void prepare(IObject query) throws TaskPrepareException;

}
