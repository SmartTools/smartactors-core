package info.smart_tools.smartactors.core.db_insert_with_key_task;

import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

public class DBInsertWIthKeyTask implements IDatabaseTask {

    private IDatabaseTask targetTask;
    private String collectionName;
    private String keyField;

    private interface prepareQuery {
    }


    public DBInsertWIthKeyTask(IDatabaseTask targetTask, String collectionName, String keyField) {
        this.targetTask = targetTask;
        this.collectionName = collectionName;
        this.keyField = keyField;
    }

    @Override
    public void prepare(IObject query) throws TaskPrepareException {

    }

    @Override
    public void execute() throws TaskExecutionException {

    }
}
