package info.smart_tools.smartactors.actors.authentication;

import info.smart_tools.smartactors.actors.authentication.exception.AuthFailException;
import info.smart_tools.smartactors.actors.authentication.wrapper.AuthenticationMessage;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import java.util.Objects;

/**
 * Actor that validate the created session
 */
public class AuthenticationActor {

    /**
     * Default constructor
     */
    public AuthenticationActor() {}

    /**
     * Validate the session
     * @param message the incoming message
     * @throws TaskExecutionException
     */
    public void authenticateSession(final AuthenticationMessage message) throws TaskExecutionException, AuthFailException {
        try {
            String requestAuthInfo = message.getRequestUserAgent();
            String sessionAuthInfo = message.getSessionUserAgent();
            if (requestAuthInfo == null || !Objects.equals(requestAuthInfo, sessionAuthInfo)) {
                throw new AuthFailException("Failed to validate session");
            }
        } catch (ReadValueException e) {
            throw new TaskExecutionException("Failed to validate session", e);
        }
    }
}
