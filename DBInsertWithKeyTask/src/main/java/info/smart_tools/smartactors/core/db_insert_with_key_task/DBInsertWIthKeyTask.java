package info.smart_tools.smartactors.core.db_insert_with_key_task;

import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.FieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import java.text.DateFormat;
import java.util.Date;

public class DBInsertWIthKeyTask implements IDatabaseTask {

    private IDatabaseTask targetTask;
    private String collectionName;

    private String documentKey = "document";
    private String startDateTimeKey = "startDateTime";
    private String isActiveKey = "isActive";

    private FieldName keyFieldName;
    private FieldName documentKeyFieldName;
    private FieldName startDateTimeFieldName;
    private FieldName isActiveFieldName;


    private IObject objectForAdd = null;

    public DBInsertWIthKeyTask(IDatabaseTask targetTask, String collectionName, String key) throws ResolutionException {
        this.targetTask = targetTask;
        this.collectionName = collectionName;
        keyFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), FieldName.class.toString()), key);
        documentKeyFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), FieldName.class.toString()), documentKey);
        startDateTimeFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), FieldName.class.toString()), startDateTimeKey);
        isActiveFieldName = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), FieldName.class.toString()), isActiveKey);
    }

    /**
     * Prepare object for adding into db
     * @param query query object
     *              <pre>
     *              {
     *                  "document"
     *              }
     *              </pre>
     * @throws TaskPrepareException
     */
    @Override
    public void prepare(IObject query) throws TaskPrepareException {

        try {
            IObject document = (IObject) query.getValue(documentKeyFieldName);
            if (document.getValue(keyFieldName) == null) {//checking that keyFieldName is not empty
                throw new TaskPrepareException("Document key is absent");
            }

            if (document.getValue(startDateTimeFieldName) == null) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                document.setValue(startDateTimeFieldName, dateFormat.format(new Date()));
            }

            targetTask.prepare(document);
        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException(e);
        }

    }

    @Override
    public void execute() throws TaskExecutionException {
        try {
            Boolean isActiveOldState = (Boolean) objectForAdd.getValue(isActiveFieldName);
            objectForAdd.setValue(isActiveFieldName, true);

            try {
                targetTask.execute();
            } catch (TaskExecutionException e) {
                objectForAdd.setValue(isActiveFieldName, isActiveOldState);
                throw new TaskExecutionException(e);
            }

        } catch (ReadValueException | ChangeValueException e) {
            throw new TaskExecutionException(e);
        }
    }
}
