package info.smart_tools.smartactors.endpoint_components_generic.outbound_message_sender;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OutboundMessageSenderTest extends TrivialPluginsLoadingTestBase {
    private IResolveDependencyStrategy channelStrategy;
    private IOutboundConnectionChannel channel;
    private IObject objectMocks[] = {
            mock(IObject.class),
            mock(IObject.class),
            mock(IObject.class),
            mock(IObject.class),
    };

    @Override
    protected void registerMocks() throws Exception {
        channelStrategy = mock(IResolveDependencyStrategy.class);

        IOC.register(Keys.getOrAdd("global outbound connection channel"), channelStrategy);

        channel = mock(IOutboundConnectionChannel.class);

        when(channelStrategy.resolve(eq("this-is-a-id"))).thenReturn(channel);
    }

    @Test public void Should_sendMessageWithExistEnvironment() throws Exception {
        OutboundMessageSender.SendEnvWrapper wrapperMock = mock(OutboundMessageSender.SendEnvWrapper.class);

        when(wrapperMock.getConnectionId()).thenReturn("this-is-a-id");
        when(wrapperMock.getEnvironment()).thenReturn(objectMocks[0]);

        new OutboundMessageSender().sendEnv(wrapperMock);

        verify(channel).send(same(objectMocks[0]));
    }

    @Test public void Should_sendMessageWithBodyAndContext() throws Exception {
        OutboundMessageSender.SendWrapper wrapperMock = mock(OutboundMessageSender.SendWrapper.class);

        when(wrapperMock.getConnectionId()).thenReturn("this-is-a-id");
        when(wrapperMock.getMessage()).thenReturn(objectMocks[1]);
        when(wrapperMock.getContext()).thenReturn(objectMocks[2]);

        new OutboundMessageSender().send(wrapperMock);

        ArgumentCaptor<IObject> argumentCaptor = ArgumentCaptor.forClass(IObject.class);

        verify(channel).send(argumentCaptor.capture());

        assertSame(
                objectMocks[1],
                argumentCaptor.getValue().getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"))
        );
        assertSame(
                objectMocks[2],
                argumentCaptor.getValue().getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"))
        );
    }
}
