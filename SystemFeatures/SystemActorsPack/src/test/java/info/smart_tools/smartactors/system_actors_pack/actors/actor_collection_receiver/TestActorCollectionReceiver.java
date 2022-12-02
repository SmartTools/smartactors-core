package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ActorCollectionReceiver}.
 */
public class TestActorCollectionReceiver extends PluginsLoadingTestBase {

    private IStrategy fullCreatorResolutionStrategy;
    private IReceiverObjectCreator[] creatorMocks;
    private IMessageProcessor[] processorMocks;
    private IObject[] childObjectConfigMocks;
    private IMessageReceiver[] childReceiverMocks;


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
        fullCreatorResolutionStrategy = mock(IStrategy.class);

        processorMocks = new IMessageProcessor[3];

        childObjectConfigMocks = new IObject[processorMocks.length];
        creatorMocks = new IReceiverObjectCreator[processorMocks.length];
        childReceiverMocks = new IMessageReceiver[processorMocks.length];

        for (int i = 0; i < processorMocks.length; i++) {
            final int fi = i;
            processorMocks[i] = mock(IMessageProcessor.class);
            childObjectConfigMocks[i] = mock(IObject.class);
            when(processorMocks[i].getSequence()).thenReturn(mock(IMessageProcessingSequence.class));
            when(processorMocks[i].getSequence().getCurrentReceiverArguments()).thenReturn(mock(IObject.class));
            when(processorMocks[i].getSequence().getCurrentReceiverArguments()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "new")))
                .thenReturn(childObjectConfigMocks[i]);
            when(processorMocks[i].getSequence().getCurrentReceiverArguments()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "key")))
                .thenReturn("in_keyField");
            when(processorMocks[i].getEnvironment()).thenReturn(mock(IObject.class));
            when(processorMocks[i].getEnvironment()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "in_keyField")))
                .thenReturn(String.valueOf(i));

            creatorMocks[i] = mock(IReceiverObjectCreator.class);
            when(fullCreatorResolutionStrategy.resolve(same(processorMocks[i].getSequence().getCurrentReceiverArguments()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "new")))))
                .thenReturn(creatorMocks[i]);
            childReceiverMocks[i] = mock(IMessageReceiver.class);
            doAnswer(invocation -> {
                invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, childReceiverMocks[fi]);
                invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
                return null;
            }).when(creatorMocks[i]).create(any(), any(), any());
        }

        IOC.register(Keys.getKeyByName("full receiver object creator"), fullCreatorResolutionStrategy);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenMapIsNull()
        throws Exception {
        new ActorCollectionReceiver(null);
    }

    @Test
    public void Should_createChildReceivers()
        throws Exception {
        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());

        receiver.receive(processorMocks[0]);
        receiver.receive(processorMocks[1]);
        receiver.receive(processorMocks[2]);
        receiver.receive(processorMocks[2]);
        receiver.receive(processorMocks[1]);
        receiver.receive(processorMocks[1]);

        verify(childReceiverMocks[0], times(1)).receive(same(processorMocks[0]));
        verify(childReceiverMocks[1], times(3)).receive(same(processorMocks[1]));
        verify(childReceiverMocks[2], times(2)).receive(same(processorMocks[2]));

        verify(creatorMocks[0], times(1)).create(any(), any(), any());
        verify(creatorMocks[1], times(1)).create(any(), any(), any());
        verify(creatorMocks[2], times(1)).create(any(), any(), any());
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_throwWhenChildCreatorReturnsMoreThanOneReceiver()
        throws Exception {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, childReceiverMocks[0]);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, childReceiverMocks[1]);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(creatorMocks[0]).create(any(), any(), any());

        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());
        receiver.receive(processorMocks[0]);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_throwWhenChildCreatorReturnsNullItem()
        throws Exception {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, null);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, childReceiverMocks[1]);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(creatorMocks[0]).create(any(), any(), any());

        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());
        receiver.receive(processorMocks[0]);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_throwWhenChildCreatorReturnsNonReceiverItem()
        throws Exception {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, new Object());
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, childReceiverMocks[1]);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(creatorMocks[0]).create(any(), any(), any());

        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());
        receiver.receive(processorMocks[0]);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_throwWhenChildCreatorReturnsNoItems()
        throws Exception {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(creatorMocks[0]).create(any(), any(), any());

        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());
        receiver.receive(processorMocks[0]);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_throwWhenChildCreatorDoesNotCall_endItems()
        throws Exception {
        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, childReceiverMocks[1]);
            return null;
        }).when(creatorMocks[0]).create(any(), any(), any());

        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());
        receiver.receive(processorMocks[0]);
    }

    @Test
    @Ignore("not working for now, reimplement")
    public void Should_resetMessageProcessorEnvironmentBeforeReceiverCall()
            throws Exception {
        doAnswer(invocation -> {
            verify(processorMocks[0]).resetEnvironment();
            return null;
        }).when(childReceiverMocks[0]).receive(same(processorMocks[0]));

        IMessageReceiver receiver = new ActorCollectionReceiver(new HashMap<>());

        receiver.receive(processorMocks[0]);

        verify(childReceiverMocks[0], times(1)).receive(same(processorMocks[0]));

        verify(creatorMocks[0], times(1)).create(any(), any(), any());
    }
}
