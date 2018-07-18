package info.smart_tools.smartactors.endpoint_components_generic.default_message_codecs.impl.json;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsonBlockDecoderTest extends TrivialPluginsLoadingTestBase {
    @Override
    protected void registerMocks() throws Exception {

    }

    private IObject testDecode(ByteBuffer byteBuffer) throws Exception {
        IDefaultMessageContext messageContext = new DefaultMessageContextImplementation();
        IInboundMessageByteArray inboundMessageByteArray = mock(IInboundMessageByteArray.class);

        when(inboundMessageByteArray.getAsByteBuffer()).thenReturn(byteBuffer);

        messageContext.setDstMessage(IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));
        messageContext.setSrcMessage(inboundMessageByteArray);

        IMessageHandlerCallback callback = mock(IMessageHandlerCallback.class);

        new JsonBlockDecoder().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));

        return (IObject) (((IObject) messageContext.getDstMessage())
                .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message")));
    }

    @Test public void Should_decodeByteArrayBuffer()
            throws Exception {
        IObject decoded = testDecode(ByteBuffer.wrap("{'foo':'bar'}".replace('\'','"').getBytes()));
        assertEquals("bar", decoded.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "foo")));
    }

    @Test public void Should_decodeDirectBuffer()
            throws Exception {
        IObject decoded;
        byte[] bytes = "{'foo':'bar'}".replace('\'','"').getBytes();
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.position(0);
        decoded = testDecode(buffer);
        assertEquals("bar", decoded.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "foo")));
    }
}
