package info.smart_tools.smartactors.endpoint.interfaces.iclient;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.net.URI;

/**
 * Parameters for configuration of client
 */
public interface IClientConfig {

    String getClientStrategy() throws ReadValueException, ChangeValueException;

    String getType() throws ReadValueException, ChangeValueException;
    void setType(String type) throws ReadValueException, ChangeValueException;

    URI getServerUri() throws ReadValueException, ChangeValueException;

    Integer getConnectionTimeout() throws ReadValueException, ChangeValueException;
    Integer getReadTimeout() throws ReadValueException, ChangeValueException;

    IClientHandler getHandler() throws ReadValueException, ChangeValueException;
    void setHandler(IClientHandler handler) throws ReadValueException, ChangeValueException;
}
