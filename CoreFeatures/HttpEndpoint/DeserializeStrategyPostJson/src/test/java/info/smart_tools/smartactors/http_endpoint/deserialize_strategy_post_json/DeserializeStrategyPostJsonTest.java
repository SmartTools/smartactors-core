package info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_json;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.net.URISyntaxException;

import static com.google.common.base.Verify.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DeserializeStrategyPostJsonTest {
    protected IMessageMapper<byte[]> mapperStub;
    @Test
    public void testDeserializationResult() throws InvalidArgumentException, ResolutionException, URISyntaxException, DeserializationException, SerializeException {
        mapperStub = mock(IMessageMapper.class);
        DeserializeStrategyPostJson deserializeStrategy = new DeserializeStrategyPostJson(mapperStub);
        when(mapperStub.deserialize(any(byte[].class))).thenReturn(new DSObject("{\"hello\": \"world\"}"));
        IObject iObject = deserializeStrategy.deserialize(new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, "http://localhost:9901"));
        String iObjectString = iObject.serialize().toString();
        verify(iObjectString.equals("{\"hello\":\"world\"}"));
    }

}
