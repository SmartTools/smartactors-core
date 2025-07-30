package info.smart_tools.smartactors.http_endpoint.deserilize_strategy_post_form_urlencoded;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_form_urlencoded.DeserializeStrategyPostFormUrlencoded;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.Test;

import static com.google.common.base.Verify.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeserializeStrategyPostFormUrlencodedTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
    }

    @Test
    public void testDeserializationResult() throws Exception {
        ByteBuf bytebuf = Unpooled.wrappedBuffer("hello=world".getBytes());
        FullHttpRequest request = mock(FullHttpRequest.class);
        when(request.content()).thenReturn(bytebuf);
        DeserializeStrategyPostFormUrlencoded deserializeStrategy = new DeserializeStrategyPostFormUrlencoded();
        IObject iObject = deserializeStrategy.deserialize(request);
        String iObjectString = iObject.serialize().toString();
        verify(iObjectString.equals("{\"hello\":\"world\"}"));
    }
}
