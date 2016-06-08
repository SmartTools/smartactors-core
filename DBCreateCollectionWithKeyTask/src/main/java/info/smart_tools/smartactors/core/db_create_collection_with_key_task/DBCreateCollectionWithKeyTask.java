package info.smart_tools.smartactors.core.db_create_collection_with_key_task;

import info.smart_tools.smartactors.core.db_create_collection_with_key_task.exceptions.CreateInstanceException;
import info.smart_tools.smartactors.core.db_create_collection_with_key_task.wrapper.CreateCollectionTargetMessage;
import info.smart_tools.smartactors.core.db_create_collection_with_key_task.wrapper.CreateCollectionWithKeyMessage;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import java.util.HashMap;
import java.util.Map;

public class DBCreateCollectionWithKeyTask implements IDatabaseTask {

    private IDatabaseTask targetTask;

    public DBCreateCollectionWithKeyTask(IDatabaseTask targetTask) throws CreateInstanceException {
        if (targetTask == null) {
            throw new CreateInstanceException("DBCreateCollectionWithKeyTask can't be created: targetTask(IDatabaseTask) is null");
        }
        this.targetTask = targetTask;
    }

    @Override
    public void prepare(IObject query) throws TaskPrepareException {
        try {
            CreateCollectionWithKeyMessage message = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), CreateCollectionWithKeyMessage.class.toString()), query);

            CreateCollectionTargetMessage targetMessage = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), CreateCollectionTargetMessage.class.toString()));

            targetMessage.setCollectionName(message.getCollectionName());

            Map<String, String> indexes = new HashMap<>();

            indexes.put(message.getKey(), Schema.KEY_INDEX_TYPE);

            targetMessage.setIndexes(indexes);

            IObject resultQuery = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), IObject.class.toString()), targetMessage);

            targetTask.prepare(resultQuery);
        } catch (ResolutionException e) {
            throw new TaskPrepareException("Can't create message Instance from query", e);
        } catch (ReadValueException e) {
            throw new TaskPrepareException("Can't read value from one of objects", e);
        } catch (ChangeValueException e) {
            throw new TaskPrepareException("Can't change value in one of objects", e);
        }
    }

    @Override
    public void execute() throws TaskExecutionException {
        targetTask.execute();
    }
}
