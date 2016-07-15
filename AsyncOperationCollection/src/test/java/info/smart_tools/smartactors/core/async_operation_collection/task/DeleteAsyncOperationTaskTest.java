package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

public class DeleteAsyncOperationTaskTest {
    private DeleteAsyncOperationTask testTask;
    private IDatabaseTask targetTask;


    @Before
    public void prepare() {
        targetTask = mock(IDatabaseTask.class);

        testTask = new DeleteAsyncOperationTask(targetTask);
    }

    @Test
    public void MustCorrectPrepare() throws TaskPrepareException {
        IObject query = mock(IObject.class);

        testTask.prepare(query);

        verify(targetTask).prepare(query);
    }

    @Test
    public void MustCorrectSetConnection() throws TaskSetConnectionException {

        IStorageConnection connection = mock(IStorageConnection.class);

        testTask.setConnection(connection);

        verify(targetTask).setConnection(connection);
    }

    @Test
    public void MustCorrectExecute() throws TaskExecutionException {
        testTask.execute();

        verify(targetTask).execute();
    }


}