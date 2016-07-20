package info.smart_tools.smartactors.actors.authentication;

import info.smart_tools.smartactors.actors.authentication.exception.AuthFailException;
import info.smart_tools.smartactors.actors.authentication.wrapper.AuthenticationMessage;
import org.junit.Before;
import org.junit.Test;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AuthenticationActorTest {
    AuthenticationActor actor;

    @Before
    public void setUp() throws Exception {
        actor = new AuthenticationActor();
    }

    @Test
    public void shouldValidateCorrectly() throws Exception {
        AuthenticationMessage message = mock(AuthenticationMessage.class);
        when(message.getRequestUserAgent()).thenReturn("123");
        when(message.getSessionUserAgent()).thenReturn("123");
        actor.authenticateSession(message);
    }

    @Test(expected = AuthFailException.class)
    public void shouldSetErrorToMessage() throws Exception {
        AuthenticationMessage message = mock(AuthenticationMessage.class);
        when(message.getRequestUserAgent()).thenReturn("123");
        when(message.getSessionUserAgent()).thenReturn("1");
        actor.authenticateSession(message);
    }

}
