package info.smart_tools.smartactors.database.cached_collection.task;

import info.smart_tools.smartactors.database.interfaces.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.field.nested_field.NestedField;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.format.DateTimeFormatter;

import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class UpsertIntoCachedCollectionTaskTest {

    private UpsertIntoCachedCollectionTask task;

    private IField startDateTimeField;
    private IField collectionNameField;
    private IField documentField;
    private IStorageConnection connection;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey formatterKey = Mockito.mock(IKey.class);
        when(Keys.getKeyByName("datetime_formatter")).thenReturn(formatterKey);
        when(IOC.resolve(eq(formatterKey))).thenReturn(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));

        startDateTimeField = mock(NestedField.class);
        collectionNameField = mock(IField.class);
        documentField = mock(IField.class);
        IKey keyField = mock(IKey.class);
        IKey keyNestedField = mock(IKey.class);
        when(Keys.getKeyByName(IField.class.getCanonicalName())).thenReturn(keyField);
        when(Keys.getKeyByName(NestedField.class.getCanonicalName())).thenReturn(keyNestedField);
        when(IOC.resolve(keyNestedField, "document/startDateTime")).thenReturn(startDateTimeField);
        when(IOC.resolve(keyField, "document")).thenReturn(documentField);
        when(IOC.resolve(keyField, "collectionName")).thenReturn(collectionNameField);

        connection = mock(IStorageConnection.class);
        task = new UpsertIntoCachedCollectionTask(connection);
    }

    @Test
    public void ShouldPrepareUpsertQuery() throws Exception {

        IObject query = mock(IObject.class);
        IObject doc = mock(IObject.class);
        when(collectionNameField.in(query)).thenReturn("collectionName");
        when(documentField.in(query)).thenReturn(doc);

        when(startDateTimeField.in(query)).thenReturn(null);
        task.prepare(query);

        verifyStatic();
        IOC.resolve(
            Keys.getKeyByName("db.collection.upsert"),
            connection,
            "collectionName",
            doc
        );
    }

    @Test(expected = TaskPrepareException.class)
    public void ShouldThrowException_When_ResolutionExceptionIsThrown() throws Exception {

        IObject rawQuery = mock(IObject.class);
        when(startDateTimeField.in(rawQuery)).thenThrow(ChangeValueException.class);

        task.prepare(rawQuery);
    }
}
