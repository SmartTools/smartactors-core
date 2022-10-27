package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.database.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.field.nested_field.NestedField;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class DeleteFromCachedCollectionTaskTest {

    private DeleteFromCachedCollectionTask task;
    private IField collectionNameField;
    private IField isActiveField;
    private IField documentField;
    private IStorageConnection connection;

    @Before
    public void setUp() throws ReadValueException, ChangeValueException, CreateCachedCollectionTaskException, ResolutionException {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        isActiveField = mock(IField.class);
        collectionNameField = mock(IField.class);
        documentField = mock(IField.class);
        IKey keyField = mock(IKey.class);
        IKey nestedKeyField = mock(IKey.class);
        when(Keys.getKeyByName(IField.class.getCanonicalName())).thenReturn(keyField);
        when(Keys.getKeyByName(NestedField.class.getCanonicalName())).thenReturn(nestedKeyField);
        when(IOC.resolve(nestedKeyField, "document/isActive")).thenReturn(isActiveField);
        when(IOC.resolve(keyField, "document")).thenReturn(documentField);
        when(IOC.resolve(keyField, "collectionName")).thenReturn(collectionNameField);
        connection = mock(IStorageConnection.class);
        task = new DeleteFromCachedCollectionTask(connection);
    }

    @Test
    public void ShouldCorrectPrepareObjectForDeleting() throws Exception {

        IObject query = mock(IObject.class);
        IObject doc = mock(IObject.class);
        when(collectionNameField.in(query)).thenReturn("collectionName");
        when(documentField.in(query)).thenReturn(doc);

        task.prepare(query);

        verify(isActiveField).out(query, false);
        verifyStatic();
        IOC.resolve(
            Keys.getKeyByName("db.collection.upsert"),
            connection,
            "collectionName",
            doc
        );
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldInCorrectPrepareObjectForDeletingWhenNestedExceptionIsGiven() throws Exception {

        IObject srcQuery = mock(IObject.class);
        doThrow(new ChangeValueException("")).when(isActiveField).out(srcQuery, false);

        task.prepare(srcQuery);
    }
}
