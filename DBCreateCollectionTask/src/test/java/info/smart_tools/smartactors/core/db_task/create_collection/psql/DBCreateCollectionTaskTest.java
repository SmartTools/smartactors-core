package info.smart_tools.smartactors.core.db_task.create_collection.psql;

import info.smart_tools.smartactors.core.db_storage.DataBaseStorage;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.PreparedQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.ConnectionPool;
import info.smart_tools.smartactors.core.db_task.create_collection.psql.wrapper.CreateCollectionQuery;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({IOC.class, DataBaseStorage.class})
@RunWith(PowerMockRunner.class)
public class DBCreateCollectionTaskTest {

    private DBCreateCollectionTask task;
    private CompiledQuery compiledQuery;
    private ConnectionPool connectionPool;

    @Before
    public void setUp() throws StorageException, IllegalAccessException {

        compiledQuery = mock(CompiledQuery.class);
        connectionPool = mock(ConnectionPool.class);

        task = new DBCreateCollectionTask(connectionPool);
    }

    @Test
    public void ShouldPrepareQuery() throws TaskPrepareException, ResolutionException, ReadValueException, ChangeValueException, StorageException {

        IObject createCollectionMessage = mock(IObject.class);
        CreateCollectionQuery message = mock(CreateCollectionQuery.class);
        PreparedQuery preparedQuery = new QueryStatement();
        initDataForPrepare(preparedQuery, message, createCollectionMessage);
        Map<String, String> indexes = new HashMap<>();
        indexes.put("meta.tags", "tags");
        when(message.getIndexes()).thenReturn(indexes);
        StorageConnection connection = mock(StorageConnection.class);
        when(connectionPool.getConnection()).thenReturn(connection);
        when(connection.compileQuery(any(PreparedQuery.class))).thenReturn(compiledQuery);

        task.prepare(createCollectionMessage);
        verify(connectionPool).getConnection();
        verify(connection).compileQuery(eq(preparedQuery));
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldThrowException_When_IndexIsInvalid() throws TaskPrepareException, ResolutionException, ReadValueException, ChangeValueException, StorageException {


        IObject createCollectionMessage = mock(IObject.class);
        CreateCollectionQuery message = mock(CreateCollectionQuery.class);
        PreparedQuery preparedQuery = new QueryStatement();
        initDataForPrepare(preparedQuery, message, createCollectionMessage);
        Map<String, String> indexes = new HashMap<>();
        indexes.put("meta.tags", "invalid");
        when(message.getIndexes()).thenReturn(indexes);
        StorageConnection connection = mock(StorageConnection.class);
        when(connectionPool.getConnection()).thenReturn(connection);
        when(connection.compileQuery(any(PreparedQuery.class))).thenReturn(compiledQuery);

        task.prepare(createCollectionMessage);
        verify(connectionPool).getConnection();
        verify(connection).compileQuery(eq(preparedQuery));
    }

    @Test
    public void ShouldExecuteQuery() throws Exception {

        mockStatic(DataBaseStorage.class);
        task.execute();

        PowerMockito.verifyStatic();
        DataBaseStorage.executeTransaction(eq(connectionPool), any());
    }

    private void initDataForPrepare(PreparedQuery preparedQuery, CreateCollectionQuery message, IObject createCollectionMessage)
        throws ResolutionException, ReadValueException, ChangeValueException {

        mockStatic(IOC.class);

        IKey key1 = mock(IKey.class);
        IKey keyQuery = mock(IKey.class);
        IKey keyMessage = mock(IKey.class);
        IKey keyFieldPath = mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(key1);
        when(IOC.resolve(eq(key1), eq(PreparedQuery.class.toString()))).thenReturn(keyQuery);
        when(IOC.resolve(eq(key1), eq(CreateCollectionQuery.class.toString()))).thenReturn(keyMessage);
        when(IOC.resolve(eq(key1), eq(FieldPath.class.toString()))).thenReturn(keyFieldPath);


        FieldPath fieldPath = mock(FieldPath.class);
        when(IOC.resolve(eq(keyQuery))).thenReturn(preparedQuery);
        when(IOC.resolve(eq(keyMessage), eq(createCollectionMessage))).thenReturn(message);
        when(IOC.resolve(eq(keyFieldPath), anyString())).thenReturn(fieldPath);
        when(fieldPath.getSQLRepresentation()).thenReturn("");

        when(message.getCollectionName()).thenReturn("collection");
    }
}
