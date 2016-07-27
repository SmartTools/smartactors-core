/**
 * Contains CreateSessionActor
 */
package info.smart_tools.smartactors.actors.create_session;

import info.smart_tools.smartactors.actors.create_session.exception.CreateSessionException;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionConfig;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionMessage;
import info.smart_tools.smartactors.actors.create_session.wrapper.Session;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.core.pool_guard.PoolGuard;
import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;

import java.util.List;

/**
 * Actor check current session, if she's null create new session
 */
public class CreateSessionActor {
    private String collectionName;
    private IPool connectionPool;
    //TODO:: in future will change dependency from core.db_search_task to core.db_task
    private IObject bufferedQuery;

    private static IField SESSION_ID_F;
    private static IField EQUALS_F;

    static {
        try {
            SESSION_ID_F = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "sessionId");
            EQUALS_F = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "$eq");

        } catch (ResolutionException e) {
           throw new RuntimeException(e);
        }
    }

    /**
     * Constructor for CreateSessionActor
     * @param config is any configurations
     * @throws CreateSessionException for any occurred error
     */
    public CreateSessionActor(final CreateSessionConfig config) throws CreateSessionException {
        try {
            this.collectionName = config.getCollectionName();
            this.connectionPool = config.getConnectionPool();
        } catch (ReadValueException e) {
            throw new CreateSessionException("Can't create Actor");
        }
    }

    /**
     * Check current session, if she's null create new session
     * @param inputMessage message for checking
     * @throws CreateSessionException Calling when throws any exception inside CreateSessionActor
     */
    public void resolveSession(final CreateSessionMessage inputMessage) throws CreateSessionException {
        try {
            String sessionId = inputMessage.getSessionId();
            if (sessionId == null || sessionId.equals("")) {
                IObject authInfo = inputMessage.getAuthInfo();
                Session newSession = IOC.resolve(Keys.getOrAdd(Session.class.getCanonicalName()));
                newSession.setAuthInfo(authInfo);
                inputMessage.setSession(newSession);
            } else {
                try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                    IDatabaseTask searchTask = IOC.resolve(Keys.getOrAdd(IDatabaseTask.class.getCanonicalName()), "PSQL");
                    IObject searchQuery = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

                    StorageConnection connection = IOC.resolve(Keys.getOrAdd(StorageConnection.class.getCanonicalName()), poolGuard.getObject());
                    prepareSearchQuery(searchQuery, inputMessage);

                    searchTask.setConnection(connection);
                    searchTask.prepare(searchQuery);
                    searchTask.execute();

                    IField bufferedQueryF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "bufferedQuery");
                    try {
                        this.bufferedQuery = bufferedQueryF.in(searchQuery);
                        if (bufferedQuery == null) {
                            throw new CreateSessionException("Search Query is null.Search task didn't returned a buffered query!");
                        }
                    } catch (InvalidArgumentException e) {
                        throw new CreateSessionException("Search task didn't returned a buffered query!", e);
                    }

                    IField countSearchResultF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "countSearchResult");
                    if (countSearchResultF.<Integer>in(searchQuery) == 0) {
                        throw new CreateSessionException("Cannot find session by sessionId: "
                                + inputMessage.getSessionId()
                        );
                    }

                    IField searchResultField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "searchResult");

                    IObject result = (searchResultField.<List<IObject>>in(searchQuery)).get(0);

                    IField sessionF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "session");
                    Session fromDBSession = sessionF.in(result);
                    if (fromDBSession == null) {
                        throw new CreateSessionException("Find session is null");
                    }
                    inputMessage.setSession(fromDBSession);

                } catch (PoolGuardException e) {
                    throw new CreateSessionException("Cannot get connection from pool.", e);
                } catch (Exception e) {
                    throw new CreateSessionException("Error during find session by sessionId: " + inputMessage.getSessionId(), e);
                }
            }
        } catch (ReadValueException | ChangeValueException e) {
            throw new CreateSessionException("Cannot create or find session by sessionId", e);
        } catch (ResolutionException e) {
            throw new CreateSessionException("Error because cannot resolve Session.class", e);
        }
    }

    private void prepareSearchQuery(final IObject searchQuery, final CreateSessionMessage inputMessage)
            throws ChangeValueException, InvalidArgumentException, ResolutionException, ReadValueException {
        IObject query = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        IObject sessionIdObject = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        EQUALS_F.out(sessionIdObject, inputMessage.getSessionId());
        SESSION_ID_F.out(query, sessionIdObject);

        IField collectionNameF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        collectionNameF.out(searchQuery, this.collectionName);
        IField pageSizeF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "pageSize");
        pageSizeF.out(searchQuery, 1);
        IField pageNumberF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "pageNumber");
        pageNumberF.out(searchQuery, 1);
        IField bufferedQueryF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "bufferedQuery");
        bufferedQueryF.out(searchQuery, this.bufferedQuery);
        IField criteriaF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "criteria");
        criteriaF.out(searchQuery, query);

    }
}
