package info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITime;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Test for {@link SaveTimestampReceiver}.
 */
public class SaveTimestampReceiverTest extends PluginsLoadingTestBase {
    private ITime timeMock;
    private IFieldName timeFieldMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        timeMock = mock(ITime.class);
        IOC.register(Keys.getKeyByName("time"), new SingletonStrategy(timeMock));
        when(timeMock.currentTimeMillis()).thenReturn(System.currentTimeMillis());

        timeFieldMock = mock(IFieldName.class);
    }

    @Test
    public void Should_saveTimeInMessageContext()
            throws Exception {
        IMessageProcessor mp = mock(IMessageProcessor.class);
        when(mp.getContext()).thenReturn(mock(IObject.class));
        SaveTimestampReceiver r = new SaveTimestampReceiver(timeFieldMock);
        r.receive(mp);
        verify(mp.getContext()).setValue(timeFieldMock, timeMock.currentTimeMillis());
        r.dispose();
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_wrapExceptions()
            throws Exception {
        IMessageProcessor mp = mock(IMessageProcessor.class);
        IObject context = mock(IObject.class);
        when(mp.getContext()).thenReturn(context);
        doThrow(ChangeValueException.class).when(context).setValue(any(), any());
        new SaveTimestampReceiver(timeFieldMock).receive(mp);
    }
}
