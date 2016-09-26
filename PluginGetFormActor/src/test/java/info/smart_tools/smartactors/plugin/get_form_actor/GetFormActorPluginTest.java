package info.smart_tools.smartactors.plugin.get_form_actor;

import info.smart_tools.smartactors.actors.get_form.GetFormActor;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.base.interfaces.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({IOC.class, Keys.class, IPoorAction.class, ApplyFunctionToArgumentsStrategy.class, GetFormActorPlugin.class, GetFormActor.class})
@RunWith(PowerMockRunner.class)
public class GetFormActorPluginTest {

    private GetFormActorPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(GetFormActor.class);

        IKey keyGeneral = mock(IKey.class);
        IKey keyPlugin = mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(keyGeneral);
        when(IOC.resolve(eq(keyGeneral), eq("GetFormActorPlugin"))).thenReturn(keyPlugin);

        bootstrap = mock(IBootstrap.class);
        plugin = new GetFormActorPlugin(bootstrap);
    }

    @Test
    public void ShouldCorrectLoadPlugin() throws Exception {

        IKey actorKey = mock(IKey.class);
        when(Keys.getOrAdd(GetFormActor.class.getCanonicalName())).thenReturn(actorKey);

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetFormActorPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetFormActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> createNewInstanceStrategyArgumentCaptor =
                ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);
        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        IOC.register(eq(actorKey), createNewInstanceStrategyArgumentCaptor.capture());

        GetFormActor actor = mock(GetFormActor.class);
        whenNew(GetFormActor.class).withAnyArguments().thenReturn(actor);

        verify(bootstrap).add(bootstrapItem);
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_BootstrapItemThrowsException() throws Exception {
        whenNew(BootstrapItem.class).withArguments("GetFormActorPlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
    }
}
