package info.smart_tools.smartactors.plugin.check_validity_async_operation_actor;

import info.smart_tools.smartactors.core.actors.check_validity_async_operation.CheckValidityAsyncOperationActor;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({IOC.class, CheckValidityAsyncOperationPlugin.class})
@RunWith(PowerMockRunner.class)
public class CheckValidityAsyncOperationPluginTest {

    private CheckValidityAsyncOperationPlugin plugin;
    private IBootstrap bootstrap;

    private IKey operationKey;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(IOC.class);

        IKey key = Mockito.mock(IKey.class);
        operationKey = Mockito.mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(key);
        PowerMockito.when(Keys.getOrAdd(CheckValidityAsyncOperationActor.class.getCanonicalName())).thenReturn(operationKey);

        bootstrap = Mockito.mock(IBootstrap.class);
        plugin = new CheckValidityAsyncOperationPlugin(bootstrap);
    }

    @Test
    public void ShouldAddNewItemDuringLoad() throws Exception {
        BootstrapItem bootstrapItem = Mockito.mock(BootstrapItem.class);
        PowerMockito.whenNew(BootstrapItem.class).withAnyArguments().thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        CheckValidityAsyncOperationActor actor = Mockito.mock(CheckValidityAsyncOperationActor.class);
        PowerMockito.whenNew(CheckValidityAsyncOperationActor.class).withAnyArguments().thenReturn(actor);

        plugin.load();

        PowerMockito.verifyNew(BootstrapItem.class).withArguments("CreateCheckValidityAsyncOperationActor");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor =
                ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);
        actionArgumentCaptor.getValue().execute();

        PowerMockito.verifyStatic();
        IOC.register(Mockito.eq(operationKey), createNewInstanceStrategyArgumentCaptor.capture());

        verify(bootstrap).add(Mockito.any());
    }
}