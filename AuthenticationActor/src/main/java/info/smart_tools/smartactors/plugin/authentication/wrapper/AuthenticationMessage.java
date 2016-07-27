package info.smart_tools.smartactors.plugin.authentication.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for message for authentication actor
 */
public interface AuthenticationMessage {
    /**
     * @return auth information from http request
     * @throws ReadValueException Throw when can't correct read value
     */
    String getRequestUserAgent() throws ReadValueException;

    /**
     * Set error to message if validation is failed
     * @return auth information from user session
     * @throws ReadValueException Throw when can't correct read value
     */
    String getSessionUserAgent() throws ReadValueException;
}
