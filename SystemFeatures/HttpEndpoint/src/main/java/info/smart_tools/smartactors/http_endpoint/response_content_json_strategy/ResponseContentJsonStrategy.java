package info.smart_tools.smartactors.http_endpoint.response_content_json_strategy;

import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_content_strategy.IResponseContentStrategy;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;

import java.nio.charset.Charset;

/**
 * Class for setting json content of the response
 */
public class ResponseContentJsonStrategy implements IResponseContentStrategy {
    @Override
    public void setContent(final IObject responseObject, final IResponse response) throws SerializeException {
        response.setContent(serializeIObjectToJson(responseObject));
    }

    private byte[] serializeIObjectToJson(final IObject responseObject) throws SerializeException {
        return ((String) responseObject.serialize()).getBytes(Charset.forName("UTF-8"));
    }
}
