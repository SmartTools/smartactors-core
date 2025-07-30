package info.smart_tools.smartactors.debugger.interfaces.exceptions;

import java.text.MessageFormat;

/**
 * Exception thrown by debugger when it cannot find the session a command should be executed within.
 */
public class SessionNotFoundException extends Exception {
    /**
     * The constructor.
     *
     * @param sessionId    identifier of the session that was not found
     */
    public SessionNotFoundException(final String sessionId) {
        super(MessageFormat.format("Could not find a session with id=''{0}''.", sessionId));
    }
}
