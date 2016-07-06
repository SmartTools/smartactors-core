package info.smart_tools.smartactors.actors.authentication.users;

import info.smart_tools.smartactors.actors.authentication.users.exceptions.AuthenticateUserException;
import info.smart_tools.smartactors.actors.authentication.users.wrappers.IUserAuthByLoginMessage;
import info.smart_tools.smartactors.actors.authentication.users.wrappers.IUserAuthByLoginParams;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_task.search.utils.IBufferedQuery;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.core.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.core.security.encoding.encoders.IEncoder;
import info.smart_tools.smartactors.core.security.encoding.encoders.IPasswordEncoder;
import info.smart_tools.smartactors.core.wrapper_generator.Field;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareForTest({ IOC.class, Keys.class })
@RunWith(PowerMockRunner.class)
public class UserAuthByLoginActorTest {
    private UserAuthByLoginActor authByLoginActor;
    private IUserAuthByLoginMessage message;
    private ISearchQuery searchQuery;
    private Field passwordField;

    private static final String COLLECTION_NAME = "testCollection";
    private static final String LOGIN = "testLogin";
    private static final String PASSWORD = "testPassword";

    private static final String AUTH_ERROR_MSG = "User authentication has been failed because: ";

    private static final String AUTH_ERROR_RESPONSE_MSG = "Такой почтовый адрес не зарегистрирован, либо пароль неверный. " +
            "Если Вы уверены, что регистрировались на нашем сайте, но не помните пароль, " +
            "то попробуйте его восстановить ниже по форме. Просто введите свой электронный адрес, " +
            "и Вам на электронную почту придет ссылка. Пройдя по ссылке, " +
            "Вы сможете ввести новый удобный для Вас пароль.";

    private boolean isSetUp = false;

    @Before
    public void setUp() throws Exception {
        if (!isSetUp) {
            isSetUp = true;

            mockStatic(IOC.class);
            mockStatic(Keys.class);

            searchQuery = mock(ISearchQuery.class);
            message = mock(IUserAuthByLoginMessage.class);

        /* <General #0> Mocks for static block. */
            Field field = mock(Field.class);
            passwordField = mock(Field.class);
            IKey fieldKey = mock(IKey.class);
            when(Keys.getOrAdd("Field")).thenReturn(fieldKey);
            when(IOC.resolve(eq(fieldKey), eq("email"))).thenReturn(field);
            when(IOC.resolve(eq(fieldKey), eq("password"))).thenReturn(passwordField);
            when(IOC.resolve(eq(fieldKey), eq("$eq"))).thenReturn(field);

        /* </General #0> Mocks for static block. */

        /* <General #1> Mocks for constructor UserAuthByLoginActor. */
            final String algorithmName = "testAlgorithm";
            final String charsetName = "testCharset";
            final String encoderName = "testEncoder";

            IUserAuthByLoginParams params = mock(IUserAuthByLoginParams.class);
            when(params.getCollection()).thenReturn(COLLECTION_NAME);
            when(params.getAlgorithm()).thenReturn(algorithmName);
            when(params.getCharset()).thenReturn(charsetName);
            when(params.getEncoder()).thenReturn(encoderName);

            IKey encoderKey = mock(IKey.class);
            IKey charSequenceCodecKey = mock(IKey.class);
            IKey passwordEncoderKey = mock(IKey.class);

            when(Keys.getOrAdd(IEncoder.class.toString() + "<-" + params.getEncoder())).thenReturn(encoderKey);
            when(Keys.getOrAdd(ICharSequenceCodec.class.toString() + "<-" + params.getCharset())).thenReturn(charSequenceCodecKey);
            when(Keys.getOrAdd(IPasswordEncoder.class.toString() + "<-MDPassword")).thenReturn(passwordEncoderKey);

            IEncoder encoder = mock(IEncoder.class);
            ICharSequenceCodec charSequenceCodec = mock(ICharSequenceCodec.class);
            IPasswordEncoder passwordEncoder = mock(IPasswordEncoder.class);

            when(IOC.resolve(eq(encoderKey))).thenReturn(encoder);
            when(IOC.resolve(eq(charSequenceCodecKey))).thenReturn(charSequenceCodec);
            when(IOC.resolve(eq(passwordEncoderKey), eq(algorithmName), eq(encoder), eq(charSequenceCodec)))
                    .thenReturn(passwordEncoder);
        /* </General #1> ------------------------------------------ */

        /* Init UserAuthByLoginActor actor. */
            authByLoginActor = new UserAuthByLoginActor(params);

        /* Sets a commons mocks. */
            setMocks();
        }
    }

    @Test
    public void should_authenticateUser_Successfully() throws Exception {
        /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
        when(message.getLogin()).thenReturn(LOGIN);
        when(message.getPassword()).thenReturn(PASSWORD);

        /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#validateLogin */
        IObject user = mock(IObject.class);
        IBufferedQuery bufferedQuery = mock(IBufferedQuery.class);
        when(searchQuery.getBufferedQuery()).thenReturn(Optional.of(bufferedQuery));
        when(searchQuery.countSearchResult()).thenReturn(1);
        when(searchQuery.getSearchResult(0)).thenReturn(user);
        /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#validateLogin */

        /* <Internal #2.2> Mocks for private method UserAuthByLoginActor#validatePassword */
        when(passwordField.out(user)).thenReturn(PASSWORD);
        /* </Internal #2.2> Mocks for private method UserAuthByLoginActor#validatePassword */
        /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
        authByLoginActor.authenticateUser(message);

        verify(searchQuery, times(1)).setCollectionName(COLLECTION_NAME);
        verify(searchQuery, times(1)).setCriteria(anyObject());
        verify(searchQuery, times(1)).setBufferedQuery(eq(null));
        verify(searchQuery, times(1)).setPageNumber(eq(1));
        verify(searchQuery, times(1)).setPageSize(eq(1));

        verify(message, times(2)).getLogin();
        verify(message, times(2)).getPassword();
        verify(message, times(1)).setAuthStatus(eq("SUCCESS"));
        verify(message, times(1)).setAuthMessage(eq(""));

        verifyStatic(times(1));
        IOC.resolve(Keys.getOrAdd(IDatabaseTask.class.toString() + "PSQL"));
        verifyStatic(times(1));
        IOC.resolve(Keys.getOrAdd(ISearchQuery.class.toString()));
        verifyStatic(times(1));
        IOC.resolve(Keys.getOrAdd("PostgresConnectionPoolGuard"));
        verifyStatic(times(2));
        IOC.resolve(Keys.getOrAdd(IObject.class.toString()));
        verifyStatic(times(1));
        IOC.resolve(Keys.getOrAdd(IObjectWrapper.class.toString() + ".getIObjects"), searchQuery);

        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(message, times(1)).setAuthStatus(statusCaptor.capture());
        verify(message, times(1)).setAuthMessage(statusMessageCaptor.capture());

        assertEquals(statusCaptor.getValue(), "SUCCESS");
        assertEquals(statusMessageCaptor.getValue(), "");
    }

    @Test
    public void should_ThrowsException_WithReason_LoginOrPasswordIsNullOrEmpty() throws Exception {
        when(message.getPassword()).thenReturn(PASSWORD);
        /* User login is null. */
        try {
            when(message.getLogin()).thenReturn(null);
            authByLoginActor.authenticateUser(message);
        } catch (InvalidArgumentException e) {
            assertEquals(e.getMessage(), "Invalid message format!");
            verifyFailAuthenticate(1);
        }
        /* User login is empty. */
        try {
            when(message.getLogin()).thenReturn("");
            authByLoginActor.authenticateUser(message);
        } catch (InvalidArgumentException e) {
            assertEquals(e.getMessage(), "Invalid message format!");
            verifyFailAuthenticate(2);
        }

        when(message.getLogin()).thenReturn(LOGIN);
        /* User password is null  */
        try {
            when(message.getPassword()).thenReturn(null);
            authByLoginActor.authenticateUser(message);
        } catch (InvalidArgumentException e) {
            assertEquals(e.getMessage(), "Invalid message format!");
            verifyFailAuthenticate(3);
        }
        /* User password is empty  */
        try {
            when(message.getPassword()).thenReturn("");
            authByLoginActor.authenticateUser(message);
        } catch (InvalidArgumentException e) {
            assertEquals(e.getMessage(), "Invalid message format!");
            verifyFailAuthenticate(4);
        }
    }

    @Test
    public void should_ThrowsException_WithReason_CachedQueryIsNotExist() throws Exception {
        try {
            /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
            when(message.getLogin()).thenReturn(LOGIN);
            when(message.getPassword()).thenReturn(PASSWORD);

            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
            when(searchQuery.getBufferedQuery()).thenReturn(Optional.empty());
            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#validateLogin */
            /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */

            authByLoginActor.authenticateUser(message);
        } catch (AuthenticateUserException e) {
            assertEquals(e.getMessage(), "Search task didn't returned a buffered query!");
            verifyFailAuthenticate(1);
        }
    }

    @Test
    public void should_ThrowsException_WithReason_UserWithGivenLoginIsNotExist() throws Exception {
        try {
            /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
            when(message.getLogin()).thenReturn(LOGIN);
            when(message.getPassword()).thenReturn(PASSWORD);

            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
            IBufferedQuery bufferedQuery = mock(IBufferedQuery.class);
            when(searchQuery.getBufferedQuery()).thenReturn(Optional.of(bufferedQuery));
            when(searchQuery.countSearchResult()).thenReturn(0);
            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
            /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */

            authByLoginActor.authenticateUser(message);
        } catch (AuthenticateUserException e) {
            assertEquals(e.getMessage(), AUTH_ERROR_MSG + "user with login: [" + LOGIN + "] doesn't exist!");
            verifyFailAuthenticate(1);
        }
    }

    @Test
    public void should_ThrowsException_WithReason_TooManyUsersWithGivenLogin() throws Exception {
        try {
            /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
            when(message.getLogin()).thenReturn(LOGIN);
            when(message.getPassword()).thenReturn(PASSWORD);

            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
            IBufferedQuery bufferedQuery = mock(IBufferedQuery.class);
            when(searchQuery.getBufferedQuery()).thenReturn(Optional.of(bufferedQuery));
            when(searchQuery.countSearchResult()).thenReturn(2);
            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
            /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */

            authByLoginActor.authenticateUser(message);
        } catch (AuthenticateUserException e) {
            assertEquals(e.getMessage(), AUTH_ERROR_MSG + "too many users with login: [" + LOGIN + "]!");
            verifyFailAuthenticate(1);
        }
    }

    @Test
    public void should_ThrowsException_WithReason_UserPasswordIsNullOrEmpty() throws Exception {
        /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
        when(message.getLogin()).thenReturn(LOGIN);
        when(message.getPassword()).thenReturn(PASSWORD);

            /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
        IObject user = mock(IObject.class);
        when(user.toString()).thenReturn("User");
        IBufferedQuery bufferedQuery = mock(IBufferedQuery.class);
        when(searchQuery.getBufferedQuery()).thenReturn(Optional.of(bufferedQuery));
        when(searchQuery.countSearchResult()).thenReturn(1);
        when(searchQuery.getSearchResult(0)).thenReturn(user);
        /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
        /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */

        try {
            when(passwordField.out(user)).thenReturn(null);
            authByLoginActor.authenticateUser(message);
        } catch (AuthenticateUserException e) {
            assertEquals(e.getMessage(), AUTH_ERROR_MSG +
                    "user with login: [" + LOGIN + "] hasn't password!");
            verifyFailAuthenticate(1);
        }

        try {
            reset(passwordField);
            when(passwordField.out(user)).thenReturn("");
            authByLoginActor.authenticateUser(message);
        } catch (AuthenticateUserException e) {
            assertEquals(e.getMessage(), AUTH_ERROR_MSG +
                    "user with login: [" + LOGIN + "] hasn't password!");
            verifyFailAuthenticate(2);
        }
    }

    @Test
    public void should_ThrowsException_WithReason_GivenUserPasswordIsInvalidForGivenLogin() throws Exception {
         /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
        when(message.getLogin()).thenReturn(LOGIN);
        when(message.getPassword()).thenReturn(PASSWORD);

        /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#resolveLogin */
        IObject user = mock(IObject.class);
        IBufferedQuery bufferedQuery = mock(IBufferedQuery.class);
        when(searchQuery.getBufferedQuery()).thenReturn(Optional.of(bufferedQuery));
        when(searchQuery.countSearchResult()).thenReturn(1);
        when(searchQuery.getSearchResult(0)).thenReturn(user);
        /* <Internal #2.1> Special mocks for private method UserAuthByLoginActor#validateLogin */

        /* <Internal #2.2> Mocks for private method UserAuthByLoginActor#resolveLogin */
        when(passwordField.out(user)).thenReturn("invalidPassword");
        /* </Internal #2.2> Mocks for private method UserAuthByLoginActor#validatePassword */
        /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */

        try {
            authByLoginActor.authenticateUser(message);
        } catch (AuthenticateUserException e) {
            assertEquals(e.getMessage(), AUTH_ERROR_MSG +
                    "Invalid password: [invalidPassword] for login: [" + LOGIN + "]!");
            verifyFailAuthenticate(1);
        }
    }


    /* Internal methods. */

    /* Resets and sets a commons mocks. */
    @After
    public void clearMocks() throws Exception {
        resetMocks();
        setMocks();
    }

    private void setMocks() throws Exception {
        when(passwordField.toString()).thenReturn("PasswordField");
        /* <General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
        IKey psqlConnectionPoolGuardKey = mock(IKey.class);
        IPoolGuard psqlConnectionPoolGuard = mock(IPoolGuard.class);
        StorageConnection psqlConnection = mock(StorageConnection.class);

        when(Keys.getOrAdd(eq("PostgresConnectionPoolGuard"))).thenReturn(psqlConnectionPoolGuardKey);
        when(IOC.resolve(eq(psqlConnectionPoolGuardKey))).thenReturn(psqlConnectionPoolGuard);
        when(psqlConnectionPoolGuard.getObject()).thenReturn(psqlConnection);

        /* <Internal #2.1> Commons mocks for private method UserAuthByLoginActor#resolveLogin */
        IKey searchTaskKey = mock(IKey.class);
        IKey searchQueryKey = mock(IKey.class);
        when(Keys.getOrAdd(IDatabaseTask.class.toString() + "PSQL")).thenReturn(searchTaskKey);
        when(Keys.getOrAdd(ISearchQuery.class.toString())).thenReturn(searchQueryKey);

        IDatabaseTask searchTask = mock(IDatabaseTask.class);
        when(IOC.resolve(eq(searchTaskKey))).thenReturn(searchTask);
        when(IOC.resolve(eq(searchQueryKey))).thenReturn(searchQuery);

        /* <Internal #2.1.1> Mocks for private method UserAuthByLoginActor#prepareQueryMsg. */
        IKey iObjectKey = mock(IKey.class);
        IKey iObjectWrappedKey = mock(IKey.class);
        when(Keys.getOrAdd(IObject.class.toString())).thenReturn(iObjectKey);
        when(Keys.getOrAdd(IObjectWrapper.class.toString() + ".getIObjects")).thenReturn(iObjectWrappedKey);

        IObject iObject = mock(IObject.class);
        when(IOC.resolve(eq(iObjectKey))).thenReturn(iObject);
        when(IOC.resolve(eq(iObjectWrappedKey), eq(searchQuery))).thenReturn(new IObject[] { iObject });
        /* </Internal #2.1.1> Mocks for private method UserAuthByLoginActor#prepareQueryMsg. */
        /* </Internal #2.1> Commons mocks for private method UserAuthByLoginActor#resolveLogin */
        /* </General #2> Mocks for method public UserAuthByLoginActor#authenticateUser. */
    }

    private void resetMocks() {
        reset(searchQuery, message, passwordField);
    }

    /* Verify message authenticate status and message when authentication is failed. */
    private void verifyFailAuthenticate(int count) {
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(message, times(count)).setAuthStatus(statusCaptor.capture());
        verify(message, times(count)).setAuthMessage(statusMessageCaptor.capture());

        assertEquals(statusCaptor.getValue(), "FAIL");
        assertEquals(statusMessageCaptor.getValue(), AUTH_ERROR_RESPONSE_MSG);
    }
}
