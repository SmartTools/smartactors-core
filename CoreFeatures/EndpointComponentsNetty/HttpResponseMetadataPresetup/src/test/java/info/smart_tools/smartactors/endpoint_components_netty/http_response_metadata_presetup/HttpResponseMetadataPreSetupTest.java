package info.smart_tools.smartactors.endpoint_components_netty.http_response_metadata_presetup;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpResponseMetadataPreSetupTest extends TrivialPluginsLoadingTestBase {
    private IObject env, context;
    private IDefaultMessageContext<?, IObject, ?> ctxMock;
    IMessageHandlerCallback<IDefaultMessageContext<?, IObject, ?>> cbMock;

    @Override
    protected void registerMocks() throws Exception {
        env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        env.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"), context);

        ctxMock = mock(IDefaultMessageContext.class);
        when(ctxMock.getDstMessage()).thenReturn(env);

        cbMock = mock(IMessageHandlerCallback.class);
    }

    @Test public void Should_setHeadersAndCookiesLists() throws Exception {
        new HttpResponseMetadataPreSetup().handle(cbMock, ctxMock);

        List hList = (List) context.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers"));
        List cList = (List) context.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "cookies"));

        assertNotNull(hList);
        assertNotNull(cList);
        assertNotSame(hList, cList);

        verify(cbMock).handle(same(ctxMock));
    }
}
