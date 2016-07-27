package info.smart_tools.smartactors.plugin.create_session;

import info.smart_tools.smartactors.actors.create_session.CreateSessionActor;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@PrepareForTest({IOC.class, Keys.class, CreateSessionPlugin.class})
@RunWith(PowerMockRunner.class)
public class CreateSessionPluginTest {

    private CreateSessionPlugin plugin;
    private IBootstrap bootstrap;

    private IKey key;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(IOC.class);
        PowerMockito.mockStatic(Keys.class);

        key = Mockito.mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(key);

        bootstrap = Mockito.mock(IBootstrap.class);
        plugin = new CreateSessionPlugin(bootstrap);
    }

    @Test
    public void ShouldAddNewItemDuringLoad() throws Exception {
        IKey actorKey = Mockito.mock(IKey.class);
        when(Keys.getOrAdd(Mockito.eq(CreateSessionActor.class.getCanonicalName()))).thenReturn(actorKey);

        BootstrapItem bootstrapItem = Mockito.mock(BootstrapItem.class);
        PowerMockito.whenNew(BootstrapItem.class).withAnyArguments().thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        PowerMockito.verifyNew(BootstrapItem.class).withArguments("CreateCreateSessionActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        Mockito.verify(bootstrapItem).process(actionArgumentCaptor.capture());

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> createNewInstanceStrategyArgumentCaptor =
                ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);
        actionArgumentCaptor.getValue().execute();

        PowerMockito.verifyStatic();
        IOC.register(Mockito.eq(actorKey), createNewInstanceStrategyArgumentCaptor.capture());

        CreateSessionActor actor = Mockito.mock(CreateSessionActor.class);
        PowerMockito.whenNew(CreateSessionActor.class).withAnyArguments().thenReturn(actor);

        Mockito.verify(bootstrap).add(Mockito.any());
    }
}