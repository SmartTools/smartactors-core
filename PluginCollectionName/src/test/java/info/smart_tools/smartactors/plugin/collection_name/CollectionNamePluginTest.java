package info.smart_tools.smartactors.plugin.collection_name;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({IOC.class, Keys.class, IPoorAction.class, ResolveByNameIocStrategy.class, CollectionNamePlugin.class, CollectionName.class})
@RunWith(PowerMockRunner.class)
public class CollectionNamePluginTest {

    private CollectionNamePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(CollectionName.class);

        IKey keyGeneral = mock(IKey.class);
        IKey keyPlugin = mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(keyGeneral);
        when(IOC.resolve(eq(keyGeneral), eq("CollectionNamePlugin"))).thenReturn(keyPlugin);

        bootstrap = mock(IBootstrap.class);
        plugin = new CollectionNamePlugin(bootstrap);
    }

    @Test
    public void ShouldCorrectLoadPlugin() throws Exception {

        IKey collectionNameKey = mock(IKey.class);
        when(Keys.getOrAdd(CollectionName.class.toString())).thenReturn(collectionNameKey);

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CollectionNamePlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        HashMap<String, CollectionName> collectionMap = mock(HashMap.class);
        whenNew(HashMap.class).withNoArguments().thenReturn(collectionMap);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CollectionNamePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        ArgumentCaptor<ResolveByNameIocStrategy> createNewInstanceStrategyArgumentCaptor =
            ArgumentCaptor.forClass(ResolveByNameIocStrategy.class);
        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        IOC.register(eq(collectionNameKey), createNewInstanceStrategyArgumentCaptor.capture());

        String collectionNameStr = "collectionName";

        CollectionName collectionName = mock(CollectionName.class);
        when(CollectionName.fromString(collectionNameStr)).thenReturn(collectionName);

        assertEquals(createNewInstanceStrategyArgumentCaptor.getValue().resolve(collectionNameStr), collectionName);

        verifyStatic();
        CollectionName.fromString(eq(collectionNameStr));

        verify(bootstrap).add(bootstrapItem);
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_BootstrapItemThrowsException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("CollectionNamePlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
    }

    @Test(expected = RuntimeException.class)
    public void ShouldThrowRuntimeException_When_LambdaThrowsException() throws Exception {

        when(Keys.getOrAdd(CollectionName.class.toString())).thenThrow(new ResolutionException(""));

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CollectionNamePlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        HashMap<String, CollectionName> collectionMap = mock(HashMap.class);
        whenNew(HashMap.class).withNoArguments().thenReturn(collectionMap);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CollectionNamePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());
        actionArgumentCaptor.getValue().execute();
    }
}
