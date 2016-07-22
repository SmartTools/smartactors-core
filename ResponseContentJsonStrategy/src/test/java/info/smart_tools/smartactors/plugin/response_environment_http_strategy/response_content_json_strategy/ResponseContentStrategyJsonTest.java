package info.smart_tools.smartactors.plugin.response_environment_http_strategy.response_content_json_strategy;


import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.iresponse.IResponse;
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
