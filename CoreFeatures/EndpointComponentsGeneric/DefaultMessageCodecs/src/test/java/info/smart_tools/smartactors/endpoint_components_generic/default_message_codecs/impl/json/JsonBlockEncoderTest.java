package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.impl.json;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.nio.ByteBuffer;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JsonBlockEncoderTest extends TrivialPluginsLoadingTestBase {
    private IDefaultMessageContext messageContext;
    private IMessageHandlerCallback callback;
    private IOutboundMessageByteArray outboundMessageByteArray;

    @Override
    protected void registerMocks() throws Exception {
        messageContext = new DefaultMessageContextImplementation();
        callback = mock(IMessageHandlerCallback.class);
        outboundMessageByteArray = mock(IOutboundMessageByteArray.class);
    }

    @Test public void Should_encodeObjectsUsingSerializationMethod() throws Exception {
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'message':{'foo':'bar'}}".replace('\'','"'));
        messageContext.setSrcMessage(env);
        messageContext.setDstMessage(outboundMessageByteArray);

        new JsonBlockEncoder().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));

        ArgumentCaptor<ByteBuffer> byteBufferArgumentCaptor = ArgumentCaptor.forClass(ByteBuffer.class);

        verify(outboundMessageByteArray).setAsByteBuffer(byteBufferArgumentCaptor.capture());

        Assert.assertEquals("{'foo':'bar'}".replace('\'', '"'),
                new String(byteBufferArgumentCaptor.getValue().array()));
    }
}
