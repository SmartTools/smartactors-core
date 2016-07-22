package info.smart_tools.smartactors.core.deserialize_strategy_post_json;

import info.smart_tools.smartactors.core.DeserializeStrategyPostJson;
import info.smart_tools.smartactors.core.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.google.common.base.Verify.verify;


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
