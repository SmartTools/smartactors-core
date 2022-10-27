package info.smart_tools.smartactors.http_endpoint.response_content_json_strategy;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class ResponseContentStrategyJsonTest {
    @Test
    public void testSerialization() throws InvalidArgumentException, SerializeException {
        String iObjectString = "{\"foo\":\"bar\"}";
        ResponseContentJsonStrategy strategy = new ResponseContentJsonStrategy();
        IResponse response = mock(IResponse.class);
        IObject responseObject = new DSObject(iObjectString);
        strategy.setContent(responseObject, response);
        verify(response, times(1)).setContent(iObjectString.getBytes(Charset.forName("UTF-8")));
    }

    @Test
    public void testEmptyObject() throws InvalidArgumentException, SerializeException {
        String iObjectString = "{}";
        ResponseContentJsonStrategy strategy = new ResponseContentJsonStrategy();
        IResponse response = mock(IResponse.class);
        IObject responseObject = new DSObject(iObjectString);
        strategy.setContent(responseObject, response);
        verify(response, times(1)).setContent(iObjectString.getBytes(Charset.forName("UTF-8")));
    }
}
