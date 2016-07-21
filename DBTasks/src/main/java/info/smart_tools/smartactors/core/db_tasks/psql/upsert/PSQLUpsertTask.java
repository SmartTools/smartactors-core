package info.smart_tools.smartactors.core.db_tasks.psql.upsert;


import info.smart_tools.smartactors.core.db_tasks.commons.DBUpsertTask;
import info.smart_tools.smartactors.core.db_tasks.psql.insert.PSQLInsertTask;
import info.smart_tools.smartactors.core.db_tasks.psql.update.PSQLUpdateTask;

/**
 * Task for upsert row to collection:
 *          1. Executes update operation if incoming query contains id;
 *          2. Executes insert operation otherwise.
 */
public class PSQLUpsertTask extends DBUpsertTask {
    private PSQLUpsertTask() {
        super();
        setInsertTask(PSQLInsertTask.create());
        setUpdatetTask(PSQLUpdateTask.create());
    }

    public static PSQLUpsertTask create() {
        return new PSQLUpsertTask();
    }
}
