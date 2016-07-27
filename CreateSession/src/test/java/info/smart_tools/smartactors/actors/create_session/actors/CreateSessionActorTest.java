package info.smart_tools.smartactors.actors.create_session.actors;

import info.smart_tools.smartactors.actors.create_session.CreateSessionActor;
import info.smart_tools.smartactors.actors.create_session.exception.CreateSessionException;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionConfig;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionMessage;
import info.smart_tools.smartactors.actors.create_session.wrapper.Session;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOC.class)
public class CreateSessionActorTest {
    private Session session;
    private CreateSessionMessage inputMessage;
    private IObject authInfo;
    private CreateSessionActor actor;
    private String collectionName = "collectionName";

    private IKey key;
    private IKey iObjectKey = mock(IKey.class);
    private IKey iFieldNameKey = mock(IKey.class);
    private IKey storageConnectionKey = mock(IKey.class);
    private IKey iDatabaseTaskKey = mock(IKey.class);
    private IKey fieldKey = mock(IKey.class);

    private IField SESSION_ID_F;
    private IField EQUALS_F;

    @org.junit.Before
    public void setUp() throws Exception {
        inputMessage = mock(CreateSessionMessage.class);
        session = mock(Session.class);
        authInfo = mock(IObject.class);
        when(inputMessage.getAuthInfo()).thenReturn(authInfo);

        mockStatic(IOC.class);
        key = mock(IKey.class);
        IKey sessionKey = mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(key);
        when(IOC.resolve(eq(key), eq(Session.class.getCanonicalName()))).thenReturn(sessionKey);
        when(IOC.resolve(eq(sessionKey))).thenReturn(session);

        CreateSessionConfig config = mock(CreateSessionConfig.class);
        when(config.getCollectionName()).thenReturn(collectionName);
        IPool connectionPool = mock(IPool.class);
        when(config.getConnectionPool()).thenReturn(connectionPool);


        //IOC keys mock
        //TODO:: change path to Field when he's merged
        when(IOC.resolve(eq(key), eq(IField.class.getCanonicalName()))).thenReturn(fieldKey);
        when(IOC.resolve(eq(key), eq(IDatabaseTask.class.getCanonicalName()))).thenReturn(iDatabaseTaskKey);
        when(IOC.resolve(eq(key), eq(StorageConnection.class.getCanonicalName()))).thenReturn(storageConnectionKey);
        when(IOC.resolve(eq(key), eq(IObject.class.getCanonicalName()))).thenReturn(iObjectKey);
        when(IOC.resolve(eq(key), eq(IFieldName.class.getCanonicalName()))).thenReturn(iFieldNameKey);

        //mock constructor
        SESSION_ID_F = mock(IField.class);
        EQUALS_F = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("sessionId"))).thenReturn(SESSION_ID_F);
        when(IOC.resolve(eq(fieldKey), eq("$eq"))).thenReturn(EQUALS_F);

        actor = new CreateSessionActor(config);
    }

    @Test
    public void Should_insertNewSessionInMessage_When_SessionIdIsNull() throws ChangeValueException, ReadValueException, CreateSessionException {
        when(inputMessage.getSessionId()).thenReturn(null);
        actor.resolveSession(inputMessage);
        verify(session).setAuthInfo(eq(authInfo));
        verify(inputMessage).setSession(eq(session));
    }

    @Test
    public void Should_insertNewSessionInMessage_When_SessionIdEqualEmptyString() throws CreateSessionException, ReadValueException, ChangeValueException {
        when(inputMessage.getSessionId()).thenReturn("");
        actor.resolveSession(inputMessage);
        verify(session).setAuthInfo(eq(authInfo));
        verify(inputMessage).setSession(eq(session));
    }

    @Test(expected = CreateSessionException.class)
    public void Should_trySearchSessionInDB_And_ThrowException_When_AnySearchQueryError() throws ResolutionException, TaskSetConnectionException, TaskPrepareException, TaskExecutionException, ChangeValueException, InvalidArgumentException, ReadValueException, CreateSessionException {
        when(inputMessage.getSessionId()).thenReturn("123");

        IDatabaseTask searchTask = mock(IDatabaseTask.class);
        when(IOC.resolve(eq(iDatabaseTaskKey), eq("PSQL"))).thenReturn(searchTask);

        IObject searchQuery = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey))).thenReturn(searchQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(IOC.resolve(eq(storageConnectionKey), anyObject())).thenReturn(connection);

        IField bufferedQueryF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("bufferedQuery"))).thenReturn(bufferedQueryF);
        when(bufferedQueryF.in(eq(searchQuery))).thenReturn(null);

        //mock for private method
        IObject query = mock(IObject.class);
        IObject sessionIdIObject = mock(IObject.class);
        Mockito.doNothing().when(EQUALS_F).out(sessionIdIObject, "123");
        Mockito.doNothing().when(SESSION_ID_F).out(query, sessionIdIObject);
        IField collectionNameF = mock(IField.class);
        IField pageSizeF = mock(IField.class);
        IField pageNumberF = mock(IField.class);
        IField criteriaF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("collectionName"))).thenReturn(collectionNameF);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeF);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberF);
        when(IOC.resolve(eq(fieldKey), eq("criteria"))).thenReturn(criteriaF);

        actor.resolveSession(inputMessage);
    }

    @Test(expected = CreateSessionException.class)
    public void Should_ThrowException_When_CountOfSearchResultIsNull() throws ResolutionException, TaskSetConnectionException, TaskPrepareException, TaskExecutionException, ChangeValueException, InvalidArgumentException, ReadValueException, CreateSessionException {
        when(inputMessage.getSessionId()).thenReturn("123");

        IDatabaseTask searchTask = mock(IDatabaseTask.class);
        when(IOC.resolve(eq(iDatabaseTaskKey), eq("PSQL"))).thenReturn(searchTask);

        IObject searchQuery = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey))).thenReturn(searchQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(IOC.resolve(eq(storageConnectionKey), anyObject())).thenReturn(connection);

        IObject bufferedQuery = mock(IObject.class);
        IField bufferedQueryF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("bufferedQuery"))).thenReturn(bufferedQueryF);
        when(bufferedQueryF.in(eq(searchQuery))).thenReturn(bufferedQuery);

        //mock for private method
        IObject query = mock(IObject.class);
        IObject sessionIdIObject = mock(IObject.class);
        Mockito.doNothing().when(EQUALS_F).out(sessionIdIObject, "123");
        Mockito.doNothing().when(SESSION_ID_F).out(query, sessionIdIObject);
        IField collectionNameF = mock(IField.class);
        IField pageSizeF = mock(IField.class);
        IField pageNumberF = mock(IField.class);
        IField criteriaF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("collectionName"))).thenReturn(collectionNameF);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeF);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberF);
        when(IOC.resolve(eq(fieldKey), eq("criteria"))).thenReturn(criteriaF);

        IField countSearchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("countSearchResult"))).thenReturn(countSearchResultF);
        when(countSearchResultF.in(searchQuery)).thenReturn(0);

        actor.resolveSession(inputMessage);

        verify(searchTask).setConnection(connection);
        verify(searchTask).prepare(searchQuery);
        verify(searchTask).execute();

        verify(collectionNameF).out(searchQuery, collectionName);
        verify(pageSizeF).out(searchQuery, 1);
        verify(pageNumberF).out(searchQuery, 1);
        verify(bufferedQueryF).out(searchQuery, bufferedQuery);
    }

    @Test(expected = CreateSessionException.class)
    public void Should_ThrowException_When_SearchResultIsEmptyList() throws Exception {
        when(inputMessage.getSessionId()).thenReturn("123");

        IDatabaseTask searchTask = mock(IDatabaseTask.class);
        when(IOC.resolve(eq(iDatabaseTaskKey), eq("PSQL"))).thenReturn(searchTask);

        IObject searchQuery = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey))).thenReturn(searchQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(IOC.resolve(eq(storageConnectionKey), anyObject())).thenReturn(connection);

        IObject bufferedQuery = mock(IObject.class);
        IField bufferedQueryF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("bufferedQuery"))).thenReturn(bufferedQueryF);
        when(bufferedQueryF.in(eq(searchQuery))).thenReturn(bufferedQuery);

        //mock for private method
        IObject query = mock(IObject.class);
        IObject sessionIdIObject = mock(IObject.class);
        Mockito.doNothing().when(EQUALS_F).out(sessionIdIObject, "123");
        Mockito.doNothing().when(SESSION_ID_F).out(query, sessionIdIObject);
        IField collectionNameF = mock(IField.class);
        IField pageSizeF = mock(IField.class);
        IField pageNumberF = mock(IField.class);
        IField criteriaF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("collectionName"))).thenReturn(collectionNameF);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeF);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberF);
        when(IOC.resolve(eq(fieldKey), eq("criteria"))).thenReturn(criteriaF);

        IField countSearchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("countSearchResult"))).thenReturn(countSearchResultF);
        when(countSearchResultF.in(searchQuery)).thenReturn(1);

        IField searchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("searchResult"))).thenReturn(searchResultF);
        when(searchResultF.in(searchQuery)).thenReturn(Collections.emptyList());

        actor.resolveSession(inputMessage);

        verify(searchTask).setConnection(connection);
        verify(searchTask).prepare(searchQuery);
        verify(searchTask).execute();
        verify(collectionNameF).out(searchQuery, collectionName);
        verify(pageSizeF).out(searchQuery, 1);
        verify(pageNumberF).out(searchQuery, 1);
        verify(bufferedQueryF).out(searchQuery, bufferedQuery);

    }

    @Test(expected = CreateSessionException.class)
    public void Should_searchSessionInDB_When_FoundSessionIsNull() throws Exception {
        when(inputMessage.getSessionId()).thenReturn("123");

        IDatabaseTask searchTask = mock(IDatabaseTask.class);
        when(IOC.resolve(eq(iDatabaseTaskKey), eq("PSQL"))).thenReturn(searchTask);

        IObject searchQuery = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey))).thenReturn(searchQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(IOC.resolve(eq(storageConnectionKey), anyObject())).thenReturn(connection);

        IObject bufferedQuery = mock(IObject.class);
        IField bufferedQueryF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("bufferedQuery"))).thenReturn(bufferedQueryF);
        when(bufferedQueryF.in(eq(searchQuery))).thenReturn(bufferedQuery);

        //mock for private method
        IObject query = mock(IObject.class);
        IObject sessionIdIObject = mock(IObject.class);
        Mockito.doNothing().when(EQUALS_F).out(sessionIdIObject, "123");
        Mockito.doNothing().when(SESSION_ID_F).out(query, sessionIdIObject);
        IField collectionNameF = mock(IField.class);
        IField pageSizeF = mock(IField.class);
        IField pageNumberF = mock(IField.class);
        IField criteriaF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("collectionName"))).thenReturn(collectionNameF);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeF);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberF);
        when(IOC.resolve(eq(fieldKey), eq("criteria"))).thenReturn(criteriaF);

        IField countSearchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("countSearchResult"))).thenReturn(countSearchResultF);
        when(countSearchResultF.in(searchQuery)).thenReturn(1);

        IField searchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("searchResult"))).thenReturn(searchResultF);
        IObject result = mock(IObject.class);
        when(searchResultF.in(searchQuery)).thenReturn(Collections.singletonList(result));

        IField sessionF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("session"))).thenReturn(sessionF);
        when(sessionF.in(result)).thenReturn(null);

        actor.resolveSession(inputMessage);

        verify(searchTask).setConnection(connection);
        verify(searchTask).prepare(searchQuery);
        verify(searchTask).execute();
        verify(collectionNameF).out(searchQuery, collectionName);
        verify(pageSizeF).out(searchQuery, 1);
        verify(pageNumberF).out(searchQuery, 1);
        verify(bufferedQueryF).out(searchQuery, bufferedQuery);

    }

    @Test
    public void Should_searchSessionInDB_When_SessionIdIsNotNullAndNotEmptyString() throws Exception {
        when(inputMessage.getSessionId()).thenReturn("123");

        IDatabaseTask searchTask = mock(IDatabaseTask.class);
        when(IOC.resolve(eq(iDatabaseTaskKey), eq("PSQL"))).thenReturn(searchTask);

        IObject searchQuery = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey))).thenReturn(searchQuery);

        StorageConnection connection = mock(StorageConnection.class);
        when(IOC.resolve(eq(storageConnectionKey), anyObject())).thenReturn(connection);

        IObject bufferedQuery = mock(IObject.class);
        IField bufferedQueryF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("bufferedQuery"))).thenReturn(bufferedQueryF);
        when(bufferedQueryF.in(eq(searchQuery))).thenReturn(bufferedQuery);

        //mock for private method
        IObject query = mock(IObject.class);
        IObject sessionIdIObject = mock(IObject.class);
        Mockito.doNothing().when(EQUALS_F).out(sessionIdIObject, "123");
        Mockito.doNothing().when(SESSION_ID_F).out(query, sessionIdIObject);
        IField collectionNameF = mock(IField.class);
        IField pageSizeF = mock(IField.class);
        IField pageNumberF = mock(IField.class);
        IField criteriaF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("collectionName"))).thenReturn(collectionNameF);
        when(IOC.resolve(eq(fieldKey), eq("pageSize"))).thenReturn(pageSizeF);
        when(IOC.resolve(eq(fieldKey), eq("pageNumber"))).thenReturn(pageNumberF);
        when(IOC.resolve(eq(fieldKey), eq("criteria"))).thenReturn(criteriaF);

        IField countSearchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("countSearchResult"))).thenReturn(countSearchResultF);
        when(countSearchResultF.in(searchQuery)).thenReturn(1);

        IField searchResultF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("searchResult"))).thenReturn(searchResultF);
        IObject result = mock(IObject.class);
        when(searchResultF.in(searchQuery)).thenReturn(Collections.singletonList(result));

        IField sessionF = mock(IField.class);
        when(IOC.resolve(eq(fieldKey), eq("session"))).thenReturn(sessionF);
        Session sessionFromDB = mock(Session.class);
        when(sessionF.in(result)).thenReturn(sessionFromDB);

        actor.resolveSession(inputMessage);

        verify(inputMessage).setSession(eq(sessionFromDB));
        verify(searchTask).setConnection(connection);
        verify(searchTask).prepare(searchQuery);
        verify(searchTask).execute();
        verify(collectionNameF).out(searchQuery, collectionName);
        verify(pageSizeF).out(searchQuery, 1);
        verify(pageNumberF).out(searchQuery, 1);
        //in private method bufferedQuery is null always
        verify(bufferedQueryF).out(searchQuery, null);
    }
}