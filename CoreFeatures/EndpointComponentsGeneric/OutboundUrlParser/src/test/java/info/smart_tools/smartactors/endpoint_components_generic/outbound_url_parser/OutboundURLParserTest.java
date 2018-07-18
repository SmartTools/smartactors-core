package info.smart_tools.smartactors.endpoint_components_generic.outbound_url_parser;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OutboundURLParserTest<TDst, TCtx> extends TrivialPluginsLoadingTestBase {
    private IDefaultMessageContext<IObject, TDst, TCtx> context;
    private IMessageHandlerCallback<IDefaultMessageContext<IObject, TDst, TCtx>> callback;

    @Override
    protected void registerMocks() throws Exception {
        context = mock(IDefaultMessageContext.class);
        callback = mock(IMessageHandlerCallback.class);

        when(context.getSrcMessage()).thenReturn(IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));
    }

    // This test takes up to 15 sec. if a hostname used
    @Test
    public void Should_parseURL() throws Exception {
        context.getSrcMessage().setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "url"),
                "http://localhost:8080/tools/smart?reallySmart=no#"
        );

        doAnswer(__ -> {
            assertEquals(
                    "http",
                    context.getSrcMessage()
                            .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "protocol"))
            );
            assertEquals(
                    8080,
                    context.getSrcMessage()
                            .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "port"))
            );
            assertEquals(
                    "/tools/smart?reallySmart=no",
                    context.getSrcMessage()
                            .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "path"))
            );
            assertEquals(
                    "localhost",
                    context.getSrcMessage()
                            .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "host"))
            );
            assertEquals(
                    new InetSocketAddress("localhost", 8080),
                    context.getSrcMessage()
                            .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address"))
            );
            return null;
        }).when(callback).handle(any());

        new OutboundURLParser<TDst, TCtx>().handle(callback, context);

        verify(callback).handle(same(context));
    }
}
