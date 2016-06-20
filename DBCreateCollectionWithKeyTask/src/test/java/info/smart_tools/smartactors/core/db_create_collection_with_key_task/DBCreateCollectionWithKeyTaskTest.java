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
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
public class DBCreateCollectionWithKeyTaskTest {

    private IDatabaseTask task;

    private DBCreateCollectionWithKeyTask testTask;

    private Key keyForKeyStorage;

    @Before
    public void prepareTaskAndOthers() throws CreateInstanceException {
        mockStatic(IOC.class);
        task = mock(IDatabaseTask.class);

        testTask = new DBCreateCollectionWithKeyTask(task);

        keyForKeyStorage = mock(Key.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(keyForKeyStorage);
    }

    @Test
    public void WhenCallExecuteMustCallExecuteInTask() throws TaskExecutionException {
        testTask.execute();
        verify(task).execute();
    }

    @Test
    public void WhenCallPrepareMustCorrectPrepareQuery() throws TaskPrepareException, ResolutionException, ReadValueException, ChangeValueException {

        IObject srcQuery = mock(IObject.class);

        CreateCollectionWithKeyMessage withKeyMessage = mock(CreateCollectionWithKeyMessage.class);

        Key withKeyMessageKey = mock(Key.class);

        when(IOC.resolve(keyForKeyStorage, CreateCollectionWithKeyMessage.class.toString())).thenReturn(withKeyMessageKey);
        when(IOC.resolve(withKeyMessageKey, srcQuery)).thenReturn(withKeyMessage);

        CreateCollectionTargetMessage targetMessage = mock(CreateCollectionTargetMessage.class);

        Key targetMessageKey = mock(Key.class);

        when(IOC.resolve(keyForKeyStorage, CreateCollectionTargetMessage.class.toString())).thenReturn(targetMessageKey);
        when(IOC.resolve(targetMessageKey)).thenReturn(targetMessage);

        String collectionName = "qwe";
        String key = "qwe2";

        when(withKeyMessage.getCollectionName()).thenReturn(collectionName);
        when(withKeyMessage.getKey()).thenReturn(key);

        testTask.prepare(srcQuery);

        verify(withKeyMessage).getCollectionName();
        verify(targetMessage).setCollectionName(collectionName);

        Map<String, String> awaitIndexes = new HashMap<>();
        awaitIndexes.put(key, Schema.KEY_INDEX_TYPE);

        verify(targetMessage).setIndexes(eq(awaitIndexes));
    }
}